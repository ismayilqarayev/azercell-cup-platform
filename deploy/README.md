# AzCup — Contabo VPS-də deploy

Bu qovluq AzCup-u Gəncə FRİTL saytı ilə **eyni serverdə, amma tam ayrı** işlədir (ayrı Docker şəbəkələri, ayrı Postgres, ayrı
volume, hüquqları azaldılmış konteyner). Səbəb: platforma şagirdlərin C++ kodunu serverdə işlədir.

| Fayl | Nə edir |
|---|---|
| `docker-compose.yml` | Postgres (`internal` şəbəkə, internetə çıxışsız) + `azcup-backend` (`internal` + `web`), sərtləşdirilmiş |
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

Xidmət adı bilərəkdən `azcup-backend`-dir (Gəncənin `backend` adı ilə toqquşmasın deyə). Caddy yalnız `azcup_web` şəbəkəsinə qoşulur: AzCup-un Postgres-inə (`internal`) çatmır, AzCup konteynerləri də Gəncənin şəbəkəsinə çatmır.

## Şagird koduna qarşı qatlar

1. **Başqa istifadəçi + limitlər** — hər yoxlama uid hovuzundan (`61000..`) bir uid götürür (`setpriv`, `prlimit`); bitəndə həmin uid-in bütün prosesləri öldürülür.
2. **Mühit təmizlənir** — proqram serverin sirlərini (JWT, DB parolu, GitHub tokeni) nə `getenv`, nə `/proc/1/environ`, nə də kompilyator xətası ilə görmür.
3. **Şəbəkə bağlıdır** — `backend/docker-entrypoint.sh` konteyner başlayanda həmin uid-lər üçün `iptables` ilə bütün trafiki `REJECT` edir (internet, loopback, Postgres), sonra `NET_ADMIN/NET_RAW/SETPCAP` hüquqlarını atır (işləyən prosesdə `CapBnd=0xc0`) — qaydaları sonradan silmək olmur. Rədd edilən `connect()` ~1 san çəkir; bu normaldir.
4. **Konteyner sərtləşdirilib** — kök fayl sistemi yalnız oxunur, `cap_drop: ALL`, `no-new-privileges`, yaddaş/CPU/proses tavanı; Gəncə şəbəkəsinə və Docker soketinə çıxış yoxdur.
5. **Sürət limiti** (`RateLimitFilter`, `LoginThrottle`) — kod icrası istifadəçi başına eyni anda 1 və dəqiqədə 20; giriş IP başına 10 dəqiqədə 200; eyni e-poçt+IP üçün 8 uğursuz cəhddən sonra 10 dəqiqə kilid. Limitlər `RATE_LIMIT_*` mühit dəyişənləri ilə dəyişdirilir. Müştəri IP-si Caddy-nin `X-Forwarded-For` başlığından götürülür (Caddy müştərinin göndərdiyini əvəz edir).

## Qalan risk

Bütün bunlara baxmayaraq şagird kodu **host nüvəsini Gəncə ilə paylaşan konteynerdə** işləyir; nüvədə hələ bilinməyən zəiflik konteynerdən çıxışa imkan verə bilər. Tam izolyasiya — ayrıca VM və ya gVisor/Firecracker kimi icra mühiti. `judge-selftest.sh` hər yeniləmədən sonra yuxarıdakı qatların işlədiyini yoxlayır.
