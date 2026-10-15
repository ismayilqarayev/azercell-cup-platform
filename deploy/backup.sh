#!/usr/bin/env bash
# AzCup üçün gündəlik Postgres ehtiyat nüsxəsi: /opt/azcup/backups altında 14 gün saxlanılır. Gəncə üçün qurulmuş ÖZƏL R2
# bucket açarları (/opt/gence-fritl/backup-r2.env) varsa, nüsxələr oraya da kopyalanır (30 gün) — yeni açar tələb olunmur.
# Bucket ictimai OLMAMALIDIR. Cron: 37 3 * * * root /opt/azcup/app/deploy/backup.sh >> /opt/azcup/backups/backup.log 2>&1
set -euo pipefail

cd "$(dirname "$0")"
set -a; . ./.env; set +a

BACKUPS=/opt/azcup/backups
# Dump-lar parol hash-ləri daxil olmaqla bütün bazanı ehtiva edir — yalnız root oxuya bilsin (700 qovluq, 600 fayl).
umask 077
mkdir -p "$BACKUPS"
chmod 700 "$BACKUPS"
out="$BACKUPS/azcup-$(date +%F-%H%M).sql.gz"

docker compose exec -T postgres pg_dump -U "$DB_USERNAME" -d "$DB_NAME" --no-owner --no-acl | gzip -9 > "$out"

# Boş/yarımçıq fayl ehtiyat sayılmasın.
[ "$(stat -c %s "$out")" -gt 1024 ] || { echo "$(date -Is) XƏTA: $out çox kiçikdir"; rm -f "$out"; exit 1; }

find "$BACKUPS" -name 'azcup-*.sql.gz' -mtime +14 -delete
echo "$(date -Is) OK $out ($(du -h "$out" | cut -f1))"

R2ENV=/opt/gence-fritl/backup-r2.env
if [ -f "$R2ENV" ] && [ -f /opt/gence-fritl/.env ]; then
  set -a; . "$R2ENV"; set +a
  # Gəncə .env-indən YALNIZ R2 ünvanı götürülür (onun DB/admin sirləri bu skriptə daxil edilmir).
  R2_ENDPOINT="$(grep -m1 '^R2_ENDPOINT=' /opt/gence-fritl/.env | cut -d= -f2-)"
  export RCLONE_CONFIG_R2_TYPE=s3 RCLONE_CONFIG_R2_PROVIDER=Cloudflare RCLONE_CONFIG_R2_NO_CHECK_BUCKET=true
  export RCLONE_CONFIG_R2_ENDPOINT="$R2_ENDPOINT"
  export RCLONE_CONFIG_R2_ACCESS_KEY_ID="$BACKUP_R2_ACCESS_KEY_ID" RCLONE_CONFIG_R2_SECRET_ACCESS_KEY="$BACKUP_R2_SECRET_ACCESS_KEY"
  RC=(docker run --rm -v "$BACKUPS":/backups:ro
      -e RCLONE_CONFIG_R2_TYPE -e RCLONE_CONFIG_R2_PROVIDER -e RCLONE_CONFIG_R2_NO_CHECK_BUCKET
      -e RCLONE_CONFIG_R2_ENDPOINT -e RCLONE_CONFIG_R2_ACCESS_KEY_ID -e RCLONE_CONFIG_R2_SECRET_ACCESS_KEY
      rclone/rclone:latest)
  if "${RC[@]}" copy /backups "R2:${BACKUP_R2_BUCKET}" --include 'azcup-*.sql.gz' \
     && "${RC[@]}" delete "R2:${BACKUP_R2_BUCKET}" --min-age 30d --include 'azcup-*.sql.gz'; then
    echo "$(date -Is) R2 OK bucket=${BACKUP_R2_BUCKET}"
  else
    echo "$(date -Is) XƏTA: R2-yə kopyalama alınmadı (yerli nüsxə saxlanıb)"
    exit 1
  fi
fi
