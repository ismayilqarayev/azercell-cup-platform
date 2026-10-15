#!/usr/bin/env bash
# GitHub Actions-dan avtomatik deploy (bax: .github/workflows/deploy.yml). Serverdə /opt/azcup/ci-deploy.sh kimi saxlanılır və
# authorized_keys-də "azcup-github-actions" açarına forced command kimi bağlanır — o açar YALNIZ bunu işlədə bilər, shell ala bilməz:
#   command="/opt/azcup/ci-deploy.sh",restrict ssh-ed25519 AAAA... azcup-github-actions
# stdin: `git archive --format=tar.gz HEAD backend deploy`; SSH_ORIGINAL_COMMAND: commit SHA (yalnız qeyd üçün).
# Bu skript tarball-dan gəlmir — dəyişsə, serverdəki nüsxəni əl ilə yeniləyin.
set -euo pipefail

APP=/opt/azcup/app
SHA="${SSH_ORIGINAL_COMMAND:-unknown}"
[[ "$SHA" =~ ^[0-9a-f]{7,40}$ ]] || SHA=unknown

# Eyni anda iki deploy getməsin.
exec 9>/opt/azcup/ci-deploy.lock
flock -w 600 9

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
cat > "$TMP/src.tar.gz"
tar tzf "$TMP/src.tar.gz" >/dev/null           # zədəli arxiv köhnə kodu yarımçıq əvəz etməsin
tar xzf "$TMP/src.tar.gz" -C "$APP"

cd "$APP/deploy"
./deploy.sh </dev/null
echo "$SHA $(date -Is)" > "$APP/.deployed-commit"

# Backend qalxana qədər gözlə (Spring ~30-60 san.).
for _ in $(seq 1 40); do
  if docker compose exec -T azcup-backend bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080' </dev/null 2>/dev/null; then
    echo "DEPLOY OK $SHA"; exit 0
  fi
  sleep 3
done
echo "XƏTA: backend 2 dəqiqədə cavab vermədi"; docker compose logs --tail 80 azcup-backend </dev/null; exit 1
