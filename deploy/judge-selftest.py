"""AzCup judge sandbox-ının SON-DAN-SONA yoxlaması (deploy/judge-selftest.sh bunu azcup_web şəbəkəsində işə salır).

Real şagird kimi davranır: qeydiyyatdan keçir, /api/run ilə C++ proqramları göndərir və onların serverin sirlərini oxuya
BİLMƏDİYİNİ yoxlayır. Hər "AÇIQ" sətri ciddi boşluqdur. Çıxış kodu: 0 = hamısı təhlükəsiz, 1 = boşluq var.
"""
import json
import secrets
import sys
import time
import urllib.error
import urllib.request

BASE = "http://azcup-backend:8080"
BASE_UID = int(sys.argv[1]) if len(sys.argv) > 1 else 61000   # uid hovuzunun başlanğıcı
POOL = int(sys.argv[2]) if len(sys.argv) > 2 else 4            # hovuzun ölçüsü
PG_IP = sys.argv[3] if len(sys.argv) > 3 else ""               # Postgres-in daxili IP-si (şəbəkə testi üçün)
EHOSTUNREACH = 113                                             # docker-entrypoint.sh-dakı "icmp-admin-prohibited" rədd kodu
SECRET_WORDS = ("JWT_SECRET", "DB_PASSWORD", "ADMIN_PASSWORD", "GITHUB_TOKEN", "DB_USERNAME")
failed = False


def call(path, body, token=None):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(BASE + path, data=json.dumps(body).encode(), headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=60) as resp:
            return resp.status, json.loads(resp.read() or b"{}")
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read() or b"{}")


def ok(msg):
    print("  OK    " + msg)


def hole(msg):
    global failed
    failed = True
    print("  AÇIQ  " + msg)


# --- 1) qeydiyyat (şagird kimi)
email = "selftest-%s@azcup.local" % secrets.token_hex(4)
status, auth = call("/api/auth/register", {"fullName": "Selftest", "email": email, "password": secrets.token_urlsafe(12), "role": "STUDENT"})
token = auth.get("token")
if not token:
    print("XƏTA: test şagirdi yaradıla bilmədi (%s %s)" % (status, auth))
    sys.exit(2)
print("Test şagirdi yaradıldı (%s)" % email)


def run(source, stdin=""):
    status, body = call("/api/run", {"sourceCode": source, "stdin": stdin}, token)
    return body.get("status"), (body.get("stdout") or ""), (body.get("stderr") or "")


def leaks(text):
    return [w for w in SECRET_WORDS if w in text]


print("1) proqram başqa istifadəçi ilə işləməlidir")
st, out, err = run('#include <unistd.h>\n#include <iostream>\nint main(){ std::cout << getuid(); }')
uid = int(out.strip()) if out.strip().isdigit() else None
(ok if uid is not None and BASE_UID <= uid < BASE_UID + POOL else hole)(
    "uid=%r (gözlənilən %d..%d), status=%s" % (out.strip(), BASE_UID, BASE_UID + POOL - 1, st))

print("2) getenv ilə sirr oxumaq mümkün olmamalıdır")
st, out, err = run('''#include <cstdlib>
#include <iostream>
int main(){
  const char* names[] = {"JWT_SECRET","DB_PASSWORD","ADMIN_PASSWORD","GITHUB_TOKEN","DB_USERNAME"};
  bool any = false;
  for (const char* n : names) { const char* v = std::getenv(n); if (v) { any = true; std::cout << n << "=" << v << "\\n"; } }
  if (!any) std::cout << "NONE";
}''')
(ok if out.strip() == "NONE" else hole)("çıxış: %r" % out[:80])

print("3) /proc/1/environ (tətbiqin mühiti) oxunmamalıdır")
st, out, err = run('''#include <fstream>
#include <iostream>
#include <iterator>
int main(){
  std::ifstream f("/proc/1/environ", std::ios::binary);
  if (!f) { std::cout << "DENIED"; return 0; }
  std::string s((std::istreambuf_iterator<char>(f)), std::istreambuf_iterator<char>());
  std::cout << "READ:" << s.size() << ":" << s.substr(0, 60);
}''')
(ok if out.strip() == "DENIED" else hole)("çıxış: %r" % out[:100])

print("4) #include \"/proc/1/environ\" kompilyasiya xətası mesajı ilə sirr sızdırmamalıdır")
st, out, err = run('#include "/proc/1/environ"\nint main(){}')
found = leaks(err) + leaks(out)
(ok if not found else hole)("status=%s, mesajda sirr sözləri: %s" % (st, found or "yoxdur"))

print("5) tətbiq kodunu/konfiqurasiyanı və sistem fayllarını dəyişə bilməməlidir")
st, out, err = run('''#include <fstream>
#include <iostream>
int main(){
  const char* paths[] = {"/app/pwned", "/etc/pwned", "/pwned"};
  for (const char* p : paths) { std::ofstream f(p); std::cout << (f ? "WROTE " : "DENIED ") << p << "\\n"; }
}''')
(ok if "WROTE" not in out else hole)("çıxış: %r" % out[:120])

print("6) şagird proqramı şəbəkəyə çıxa bilməməlidir (internet, backend-in özü, Postgres)")
# Qayda olmasaydı hər üç IPv4 qoşulma UĞURLU olardı (8080-də backend, 5432-də Postgres dinləyir; internet açıqdır) — yəni
# "BLOCKED 113" yalnız iptables qaydasının işlədiyini sübut edir (113 = EHOSTUNREACH, entrypoint-dəki rədd növü).
NET_PROBE = r'''#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <unistd.h>
#include <cerrno>
#include <iostream>
#include <string>

// Hədəf stdin-dən oxunur ("ip port"). Rədd edilən qoşulma ~1 san çəkir (nüvə ICMP xətasını SYN-in ilk təkrarına qədər
// gözlədir), icra vaxt limiti isə 3 san-dir — ona görə hər hədəf AYRI icrada yoxlanılır.
int main() {
  std::string ip; int port = 0;
  std::cin >> ip >> port;
  bool v6 = ip.find(':') != std::string::npos;
  int fd = socket(v6 ? AF_INET6 : AF_INET, SOCK_STREAM, 0);
  if (fd < 0) { std::cout << "NOSOCKET " << errno << "\n"; return 0; }
  timeval tv{2, 0};
  setsockopt(fd, SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof tv);
  int rc;
  if (v6) {
    sockaddr_in6 a{}; a.sin6_family = AF_INET6; a.sin6_port = htons(port); inet_pton(AF_INET6, ip.c_str(), &a.sin6_addr);
    rc = connect(fd, (sockaddr*)&a, sizeof a);
  } else {
    sockaddr_in a{}; a.sin_family = AF_INET; a.sin_port = htons(port); inet_pton(AF_INET, ip.c_str(), &a.sin_addr);
    rc = connect(fd, (sockaddr*)&a, sizeof a);
  }
  if (rc == 0) std::cout << "CONNECTED\n"; else std::cout << "BLOCKED " << errno << "\n";
  close(fd);
}
'''
if not PG_IP:
    hole("Postgres IP-si verilməyib (judge-selftest.sh bunu ötürməlidir)")
else:
    targets = [("loopback", "127.0.0.1", 8080), ("internet", "1.1.1.1", 443), ("postgres", PG_IP, 5432)]
    for label, ip, port in targets:
        st, out, err = run(NET_PROBE, "%s %d" % (ip, port))
        got = out.split()
        (ok if got == ["BLOCKED", str(EHOSTUNREACH)] else hole)("%s (%s:%d): %s [status=%s]" % (label, ip, port, out.strip() or "nəticə yoxdur", st))
    # IPv6 konteynerdə söndürülü ola bilər (NOSOCKET) və ya başqa kodla rədd edilə bilər — əsas odur ki, QOŞULMASIN.
    st, out, err = run(NET_PROBE, "::1 8080")
    got6 = out.split()
    (ok if got6 and got6[0] != "CONNECTED" else hole)("loopback6 (::1:8080): %s [status=%s]" % (out.strip() or "nəticə yoxdur", st))

print("7) fork-bomba serveri çökdürməməlidir (limitlər + vaxt aşımı)")
t0 = time.time()
st, out, err = run('#include <unistd.h>\nint main(){ for(;;) fork(); }')
print("  (məlumat) status=%s, %.1f san" % (st, time.time() - t0))
time.sleep(1)  # yoxlama bitəndə uid-in bütün prosesləri öldürülür — gözləməyə ehtiyac yoxdur
st2, out2, _ = run('#include <iostream>\nint main(){ std::cout << "alive"; }')
(ok if out2.strip() == "alive" else hole)("bombadan sonra yoxlayıcı işləyir: status=%s çıxış=%r" % (st2, out2.strip()))

print()
print("NƏTİCƏ: " + ("BOŞLUQ VAR ✘" if failed else "sandbox işləyir ✔"))
sys.exit(1 if failed else 0)
