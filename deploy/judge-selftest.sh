#!/usr/bin/env bash
# Şagird kodu sandbox-ının SERVERDƏ (Docker-də) real işlədiyini yoxlayır: konteyner sərtləşdirməsini baxır, sonra real şagird
# kimi qeydiyyatdan keçib /api/run ilə sirləri oxumağa çalışan C++ proqramlar göndərir (bax: judge-selftest.py).
# DİQQƏT: test şagirdi bazaya yazılır — Neon-dan bərpadan ƏVVƏL işlədin (bərpa bazanı sıfırlayır) və ya sonra əl ilə silin.
# İşlətmək: cd /opt/azcup/app/deploy && ./judge-selftest.sh      Nəticə kodu 0 = təhlükəsiz, ≠0 = boşluq var.
set -uo pipefail

cd "$(dirname "$0")"
FAIL=0
RUN_UID="$(grep -m1 'JUDGE_RUN_AS_UID' docker-compose.yml | grep -oE '[0-9]+' | head -1)"; RUN_UID="${RUN_UID:-61000}"
POOL="$(grep -m1 'JUDGE_UID_POOL' docker-compose.yml | grep -oE '[0-9]+' | head -1)"; POOL="${POOL:-4}"

echo "== Konteyner sərtləşdirməsi"
C="$(docker compose ps -q backend)"
[ -n "$C" ] || { echo "XƏTA: backend konteyneri işləmir"; exit 2; }
chk() { # ad, gözlənilən-regex, əmr...
  local name="$1" want="$2"; shift 2
  local got; got="$("$@" 2>&1 | tr -d '\n' || true)"
  if echo "$got" | grep -qE "$want"; then echo "  OK    $name ($got)"; else echo "  AÇIQ  $name (gözlənilən $want, alınan: $got)"; FAIL=1; fi
}
chk "kök fayl sistemi yalnız oxunur"   '^true$'  docker inspect -f '{{.HostConfig.ReadonlyRootfs}}' "$C"
chk "no-new-privileges açıqdır"        'no-new-privileges' docker inspect -f '{{.HostConfig.SecurityOpt}}' "$C"
chk "hüquqlar alınıb (cap_drop ALL)"   'ALL'     docker inspect -f '{{.HostConfig.CapDrop}}' "$C"
chk "yalnız SETUID/SETGID qaytarılıb"  '^\[(CAP_)?(SETUID (CAP_)?SETGID|SETGID (CAP_)?SETUID)\]$' docker inspect -f '{{.HostConfig.CapAdd}}' "$C"
chk "proses sayı tavanı qoyulub"       '^512$'   docker inspect -f '{{.HostConfig.PidsLimit}}' "$C"
chk "yaddaş tavanı 2 GB"               '^2147483648$' docker inspect -f '{{.HostConfig.Memory}}' "$C"
chk "Docker soketi qoşulmayıb"         '^$'      sh -c "docker inspect -f '{{range .Mounts}}{{.Source}} {{end}}' $C | tr ' ' '\n' | grep -E 'docker.sock' || true"
chk "Gəncə şəbəkəsinə qoşulmayıb"      '^$'      sh -c "docker inspect -f '{{range \$k,\$v := .NetworkSettings.Networks}}{{\$k}} {{end}}' $C | tr ' ' '\n' | grep -E 'gence' || true"

echo "== Real şagird kimi hücum cəhdləri (judge-selftest.py)"
docker run --rm --network azcup_web -v "$PWD/judge-selftest.py":/t.py:ro python:3-alpine python /t.py "$RUN_UID" "$POOL" || FAIL=1

echo
[ "$FAIL" = 0 ] && echo "ÜMUMİ NƏTİCƏ: təhlükəsizdir ✔" || echo "ÜMUMİ NƏTİCƏ: BOŞLUQ VAR ✘ — canlıya buraxmayın"
exit "$FAIL"
