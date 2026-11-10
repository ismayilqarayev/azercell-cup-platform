#!/usr/bin/env bash
# Repodan silinmiş mənbə fayllarını serverdəki backend/src qovluğundan silir.
#
# Niyə lazımdır: CI arxivi /opt/azcup/app qovluğunun ÜSTÜNƏ açır (bax: ci-deploy.sh), yəni repodan silinən fayl serverdə qalır.
# Silinmiş bir Java sinfi orada qalanda Docker yığımı "cannot find symbol" ilə yıxılırdı (2026-11-10: QuizSeedLoader.java).
#
# İstifadə: prune-stale-sources.sh <backend qovluğu> <manifest>
# Manifest CI-da yaranır (bax: .github/workflows/deploy.yml): arxivdəki backend/src fayllarının "src/..." şəklində siyahısı,
# son sətirdə isə "#END <fayl sayı>" möhürü. Yalnız <backend>/src altındakı, manifestdə OLMAYAN fayllar silinir;
# deploy/.env kimi serverə məxsus fayllara toxunulmur.
#
# Skript şübhəli vəziyyətdə HEÇ NƏ silmir və xəta ilə çıxır (deploy dayanır, köhnə konteyner işləməyə davam edir):
#  - möhür yoxdur və ya say uyğun gəlmir (siyahı yarımçıq/kəsilmişdir);
#  - siyahıda src/ ilə başlamayan sətir var və ya əsas sinif (BackendApplication.java) yoxdur;
#  - siyahıdakı hər hansı fayl diskdə yoxdur (arxiv indicə açılıb, hamısı olmalıdır);
#  - silinəcək fayllar gözlənilməz dərəcədə çoxdur (həm 50-dən, həm də ağacın 30%-indən çox).
set -euo pipefail
export LC_ALL=C

BACKEND="${1:?backend qovluğu verilməyib}"
MANIFEST="$(cd "$(dirname "${2:?manifest verilməyib}")" && pwd)/$(basename "$2")"

fail() { echo "XƏTA: $1 Heç nə silinmədi."; exit 1; }

[ -d "$BACKEND/src" ] || fail "$BACKEND/src tapılmadı."
[ -s "$MANIFEST" ] || fail "manifest boşdur və ya yoxdur: $MANIFEST."
cd "$BACKEND"

# 1) Bütövlük möhürü: son sətir "#END <say>" olmalı və say siyahıdakı sətirlərin sayına bərabər olmalıdır.
trailer="$(tail -n 1 "$MANIFEST")"
[[ "$trailer" =~ ^#END\ ([0-9]+)$ ]] || fail "manifestin sonunda #END möhürü yoxdur (siyahı yarımçıqdır)."
expected="${BASH_REMATCH[1]}"
LIST="$(mktemp)"
STALE="$(mktemp)"
trap 'rm -f "$LIST" "$STALE"' EXIT
grep -v '^#END ' "$MANIFEST" | sort > "$LIST"
actual="$(wc -l < "$LIST" | tr -d ' ')"
[ "$actual" -eq "$expected" ] || fail "manifestdə $actual sətir var, möhürdə $expected yazılıb."
[ "$actual" -gt 0 ] || fail "manifest boşdur."

# 2) Məzmun: yalnız src/ yolları, əsas sinif yerində.
if grep -qvE '^src/' "$LIST"; then fail "manifestdə src/ ilə başlamayan sətir var."; fi
grep -qx 'src/main/java/az/azcup/backend/BackendApplication.java' "$LIST" || fail "manifestdə BackendApplication.java yoxdur."

# 3) Arxiv indicə açılıb, ona görə siyahıdakı HƏR fayl diskdə olmalıdır; biri belə yoxdursa siyahı bu ağaca aid deyil.
missing=0
while IFS= read -r f; do
  if [ ! -f "$f" ]; then
    missing=$((missing + 1))
    if [ "$missing" -le 5 ]; then echo "siyahıda var, diskdə yoxdur: $f"; fi
  fi
done < "$LIST"
[ "$missing" -eq 0 ] || fail "siyahıdakı $missing fayl diskdə yoxdur."

# 4) Silinəcəklər: diskdə olub siyahıda olmayanlar. Kütləvi silməyə qarşı hədd.
comm -23 <(find src -type f | sort) "$LIST" > "$STALE"
stale="$(wc -l < "$STALE" | tr -d ' ')"
on_disk="$(find src -type f | wc -l | tr -d ' ')"
if [ "$stale" -gt 50 ] && [ $((stale * 100)) -gt $((on_disk * 30)) ]; then
  fail "silinəcək fayl sayı şübhəli dərəcədə çoxdur ($stale / $on_disk)."
fi

removed=0
while IFS= read -r f; do
  case "$f" in
    src/*) ;;
    *) continue ;;
  esac
  rm -f -- "$f"
  echo "silindi (repoda artıq yoxdur): $f"
  removed=$((removed + 1))
done < "$STALE"

find src -depth -type d -empty -delete
echo "köhnə mənbə faylları: $removed silindi"
