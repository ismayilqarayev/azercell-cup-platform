#!/usr/bin/env bash
# AzCup bazasını Neon-dan bu serverin Postgres-inə köçürür. Əvvəl Neon-dan fayla yüklənir və YOXLANIR; yalnız uğurlu olsa bu
# serverin cari bazası silinib həmin fayldan doldurulur. Neon bağlantı sətri gizli daxil edilir, heç yerdə saxlanmır.
# İşlətmək: ssh -t root@<server> /opt/azcup/app/deploy/restore-from-neon.sh   (çıxış /opt/azcup/restore.log-a da yazılır)
set -euo pipefail

cd "$(dirname "$0")"
LOG=/opt/azcup/restore.log
exec > >(tee -a "$LOG") 2>&1
echo "=== $(date -Is) restore başladı ==="

set -a; . ./.env; set +a
DUMP=/opt/azcup/backups/neon-dump.sql

read -rsp "Neon bağlantı sətri (yapışdırın, yazı görünməyəcək, sonra Enter): " RAW; echo
echo "Alındı: ${#RAW} simvol"
# "psql '...'", "DATABASE_URL=..." və dırnaqlı formatlardan yalnız postgresql://... hissəsini çıxarır.
NEON="$(printf '%s' "$RAW" | grep -oE "postgres(ql)?://[^[:space:]'\"]+" | head -n 1 || true)"
if [ -z "$NEON" ]; then
  echo "XƏTA: yapışdırılan mətndə postgresql://... sətri tapılmadı (ilk 8 simvol: ${RAW:0:8})"
  exit 1
fi
export NEON

PSQL=(docker compose exec -T postgres psql -q -v ON_ERROR_STOP=1 -U "$DB_USERNAME" -d "$DB_NAME")

mkdir -p /opt/azcup/backups
umask 077
echo "==> Neon-dan fayla yüklənir"
docker run --rm -e NEON postgres:18 sh -c 'pg_dump --no-owner --no-acl "$NEON"' > "$DUMP"
size=$(stat -c %s "$DUMP")
echo "Yüklənən fayl: ${size} bayt"
if [ "$size" -lt 10000 ] || ! tail -n 5 "$DUMP" | grep -q "PostgreSQL database dump complete"; then
  echo "XƏTA: Neon-dan yükləmə uğursuz oldu — bu serverin bazasına TOXUNULMADI"
  rm -f "$DUMP"
  exit 1
fi

# Köçürmədən sonra bu serverin bazası CANLI olur — təsadüfən yenidən işlədilsə yeni məlumatı silməsin.
echo "Bu serverin cari bazası SİLİNƏCƏK. Hazırkı istifadəçi sayı: $("${PSQL[@]}" -At -c 'SELECT count(*) FROM users' </dev/null 2>/dev/null || echo 'yoxdur')"
read -rp "Davam etmək üçün SIL yazın: " CONFIRM
[ "$CONFIRM" = "SIL" ] || { echo "Ləğv edildi, heç nə dəyişmədi"; exit 1; }

echo "==> backend dayandırılır, köhnə sxem silinir"
docker compose stop backend
"${PSQL[@]}" -c "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;"

echo "==> fayldan bərpa olunur"
"${PSQL[@]}" < "$DUMP"

echo "==> cədvəllər və sətir sayları"
for t in $("${PSQL[@]}" -At -c "SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY 1" </dev/null); do
  echo "$t: $("${PSQL[@]}" -At -c "SELECT count(*) FROM \"$t\"" </dev/null)"
done

echo "==> backend başladılır"
docker compose up -d backend
echo "HAZIRDIR. Köhnə admin parolu bazadan gəlir — yeni .env parolu mövcud admin hesabını DƏYİŞMİR: sayta daxil olub admin parolunu dəyişin."
