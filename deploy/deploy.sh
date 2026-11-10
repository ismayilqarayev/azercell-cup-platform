#!/usr/bin/env bash
# Kodu yeniləyir və AzCup konteynerlərini yenidən qurub başladır (serverdə: /opt/azcup/app/deploy).
# /opt/azcup/app git klonudursa əvvəl `git pull --ff-only` edir; yeni image uğurlu qurulmasa köhnə konteyner qalır.
set -euo pipefail

cd "$(dirname "$0")"
APP=/opt/azcup/app
if [ -d "$APP/.git" ]; then
  git -C "$APP" pull --ff-only
fi

# CI ilə gələn arxiv köhnə qovluğun üstünə açılır, ona görə repodan silinmiş mənbə faylları burada qalırdı və yığımı sındırırdı.
# CI arxivə backend/src fayllarının siyahısını qoyur; siyahıda olmayan fayllar yığımdan ƏVVƏL silinir (bax: prune-stale-sources.sh).
# Git klonu ilə deploy-da (yuxarıdakı pull) siyahı olmur və bu addım atlanır: git silinmiş faylları özü götürür.
if [ -f ./backend-src.manifest ]; then
  bash ./prune-stale-sources.sh "$APP/backend" ./backend-src.manifest
fi

docker network inspect azcup_web >/dev/null 2>&1 || docker network create azcup_web >/dev/null
docker compose up -d --build
docker image prune -f >/dev/null
docker compose ps
echo "HAZIRDIR $(date -Is)"
