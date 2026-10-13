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


def run(source):
    status, body = call("/api/run", {"sourceCode": source, "stdin": ""}, token)
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

print("6) fork-bomba serveri çökdürməməlidir (limitlər + vaxt aşımı)")
t0 = time.time()
st, out, err = run('#include <unistd.h>\nint main(){ for(;;) fork(); }')
print("  (məlumat) status=%s, %.1f san" % (st, time.time() - t0))
time.sleep(1)  # yoxlama bitəndə uid-in bütün prosesləri öldürülür — gözləməyə ehtiyac yoxdur
st2, out2, _ = run('#include <iostream>\nint main(){ std::cout << "alive"; }')
(ok if out2.strip() == "alive" else hole)("bombadan sonra yoxlayıcı işləyir: status=%s çıxış=%r" % (st2, out2.strip()))

print()
print("NƏTİCƏ: " + ("BOŞLUQ VAR ✘" if failed else "sandbox işləyir ✔"))
sys.exit(1 if failed else 0)
