#!/usr/bin/env bash
# AzCup üçün ilk quraşdırma: .env faylını yaradır. DB parolu və JWT_SECRET serverdə TƏSADÜFİ yaranır (heç yerdə göstərilmir);
# Admin e-poçtu/parolu və (ixtiyari) GITHUB_TOKEN gizli sorğu ilə daxil edilir. Sirlər ekrana yazılmır, tarixçəyə düşmür.
# İşlətmək: ssh -t root@<server> /opt/azcup/app/deploy/install.sh
set -euo pipefail

cd "$(dirname "$0")"
if [ -f .env ]; then
  echo "XƏTA: .env artıq var. Üstünə yazmıram (köhnə dəyərlər itməsin). Dəyişmək üçün əvvəl əl ilə silin: $(pwd)/.env"
  exit 1
fi

command -v openssl >/dev/null || { echo "XƏTA: openssl yoxdur"; exit 1; }
# Caddy ilə ortaq şəbəkə (compose onu external kimi gözləyir).
docker network inspect azcup_web >/dev/null 2>&1 || docker network create azcup_web >/dev/null

read -rp "Admin e-poçtu (giriş üçün): " ADMIN_EMAIL
read -rsp "Admin parolu (ən azı 12 simvol, yazı görünməyəcək): " ADMIN_PASSWORD; echo
read -rsp "Admin parolu (təkrar): " ADMIN_PASSWORD2; echo
[ "$ADMIN_PASSWORD" = "$ADMIN_PASSWORD2" ] || { echo "XƏTA: parollar eyni deyil, heç nə yazılmadı"; exit 1; }
[ "${#ADMIN_PASSWORD}" -ge 12 ] || { echo "XƏTA: parol ən azı 12 simvol olmalıdır, heç nə yazılmadı"; exit 1; }
read -rsp "GitHub token (kod nümunələrini çəkmək üçün; YENİ, yalnız oxuma icazəli token; boş buraxmaq olar): " GITHUB_TOKEN; echo

# Dəyərlərdə sətir sonu/boşluq olmasın (.env formatını pozmasın).
clean() { printf '%s' "$1" | tr -d '\r\n'; }
ADMIN_EMAIL="$(clean "$ADMIN_EMAIL")"; ADMIN_PASSWORD="$(clean "$ADMIN_PASSWORD")"; GITHUB_TOKEN="$(clean "$GITHUB_TOKEN")"
case "$ADMIN_PASSWORD$GITHUB_TOKEN$ADMIN_EMAIL" in *\$*|*\"*|*\'*|*\\*|*\`*|*\ *)
  echo "XƏTA: dəyərlərdə \$ \" ' \\ \` və ya boşluq ola bilməz (compose .env faylını oxuyarkən pozular). Başqa parol seçin."; exit 1;;
esac

umask 077
{
  echo "DB_NAME=azcup"
  echo "DB_USERNAME=azcup"
  echo "DB_PASSWORD=$(openssl rand -hex 24)"
  echo "JWT_SECRET=$(openssl rand -hex 48)"
  echo "ADMIN_EMAIL=$ADMIN_EMAIL"
  echo "ADMIN_PASSWORD=$ADMIN_PASSWORD"
  echo "GITHUB_TOKEN=$GITHUB_TOKEN"
} > .env
chmod 600 .env
echo "HAZIRDIR: $(pwd)/.env yaradıldı (yalnız root oxuya bilir)."
echo "Növbəti: docker compose up -d --build  (sonra bazanı Neon-dan köçürmək üçün restore-from-neon.sh)"
