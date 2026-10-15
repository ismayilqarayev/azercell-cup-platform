#!/usr/bin/env bash
# Kodu yeniləyir və AzCup konteynerlərini yenidən qurub başladır (serverdə: /opt/azcup/app/deploy).
# /opt/azcup/app git klonudursa əvvəl `git pull --ff-only` edir; yeni image uğurlu qurulmasa köhnə konteyner qalır.
set -euo pipefail

cd "$(dirname "$0")"
APP=/opt/azcup/app
if [ -d "$APP/.git" ]; then
  git -C "$APP" pull --ff-only
fi

docker network inspect azcup_web >/dev/null 2>&1 || docker network create azcup_web >/dev/null
docker compose up -d --build
docker image prune -f >/dev/null
docker compose ps
echo "HAZIRDIR $(date -Is)"
