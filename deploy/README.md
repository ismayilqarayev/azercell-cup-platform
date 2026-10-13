# AzCup — Contabo VPS-də deploy

Bu qovluq AzCup-u Gəncə FRİTL saytı ilə **eyni serverdə, amma tam ayrı** işlədir (ayrı Docker şəbəkələri, ayrı Postgres, ayrı
volume, hüquqları azaldılmış konteyner). Səbəb: platforma şagirdlərin C++ kodunu serverdə işlədir.

| Fayl | Nə edir |
|---|---|
| `docker-compose.yml` | Postgres (`internal` şəbəkə, internetə çıxışsız) + backend (`internal` + `web`), sərtləşdirilmiş |
| `install.sh` | İlk quraşdırma: `.env` yaradır (DB parolu və JWT_SECRET təsadüfi; admin və GITHUB_TOKEN gizli sorğu ilə) |
| `restore-from-neon.sh` | Bazanı Neon-dan köçürür (bağlantı sətri gizli daxil edilir) |
| `backup.sh` | Gündəlik `pg_dump` (14 gün yerli; Gəncənin özəl R2 bucket-i varsa 30 gün orada) |
| `deploy.sh` | `git pull` + `docker compose up -d --build` |
| `judge-selftest.sh/.py` | Sandbox-ın işlədiyini real hücum cəhdləri ilə yoxlayır |

## Qaydası (server: `/opt/azcup`)

1. `git clone https://github.com/ismayilqarayev/azercell-cup-platform /opt/azcup/app` (`backups/` qovluğu `/opt/azcup/backups`).
2. `cd /opt/azcup/app/deploy && ./install.sh` (`azcup_web` şəbəkəsini də yaradır) → sonra `docker compose up -d --build`.
3. `./judge-selftest.sh` — **bütün sətirlər OK olmalıdır** (bazanı bərpa etməzdən əvvəl işlədin).
4. `./restore-from-neon.sh` — Neon bazasını gətirir. Köhnə admin parolu bazadan gəlir, saytda admin parolunu dəyişin.
5. Cron: `37 3 * * * root /opt/azcup/app/deploy/backup.sh >> /opt/azcup/backups/backup.log 2>&1`

## Caddy (Gəncə stack-ində, HTTPS və yönləndirmə)

`/opt/gence-fritl/docker-compose.yml`-də `caddy` xidmətinə `networks: [default, azcup_web]` əlavə olunur (`azcup_web` —
`external: true`), `Caddyfile`-a isə:

```
azcup.gencefritl.com {
	encode zstd gzip
	request_body { max_size 2MB }
	reverse_proxy azcup-backend:8080
}
```

Caddy yalnız `azcup_web` şəbəkəsinə qoşulur: AzCup-un Postgres-inə (`internal`) çatmır, AzCup konteynerləri də Gəncənin şəbəkəsinə çatmır.

## Qalan risk

Şagird proqramı çıxış şəbəkəsinə malikdir (məs. internetə sorğu göndərə bilər) və paralel proqramlar eyni uid ilə işləyir.
Tam həll — şəbəkəsiz, ayrıca icra konteyneri.
