#!/bin/sh
# AzCup konteynerinin giriş nöqtəsi.
#
# JUDGE_RUN_AS_UID təyin olunubsa (VPS-də), şagird kodunun işlədiyi uid-lər (JudgeSandbox hovuzu) üçün konteynerin bütün
# şəbəkə trafikini bağlayır: internet, loopback (backend-in özü), Docker şəbəkələri və Postgres. Şagird proqramının şəbəkəyə
# ehtiyacı yoxdur; bağlanmasa o, sirləri/bazanı şəbəkə üzərindən oxuya və ya serveri hücum üçün istifadə edə bilərdi.
#
# Qaydaları qoymaq üçün konteyner müvəqqəti NET_ADMIN/NET_RAW/SETPCAP hüquqları ilə başlayır; qaydalar qoyulandan sonra bu
# hüquqlar bounding set-dən ATILIR və Java onlarsız (yalnız SETUID/SETGID ilə) başlayır — şagird kodu (və ya Java prosesinin
# özü) sonradan qaydaları silə bilməz.
#
# JUDGE_RUN_AS_UID təyin olunmayıbsa (lokal, Render) heç nə dəyişmir, əmr olduğu kimi işlədilir.
set -eu

if [ -z "${JUDGE_RUN_AS_UID:-}" ]; then
    exec "$@"
fi

first="$JUDGE_RUN_AS_UID"
last=$((first + ${JUDGE_UID_POOL:-1} - 1))
if [ "$first" = "$last" ]; then range="$first"; else range="$first-$last"; fi

# Kök fayl sistemi yalnız oxunandır — iptables-ın kilid faylı /tmp-də (tmpfs) olmalıdır.
export XTABLES_LOCKFILE=/tmp/xtables.lock

block_egress() { # $1 = iptables|ip6tables, $2 = reject-with növü
    "$1" -C OUTPUT -m owner --uid-owner "$range" -j REJECT --reject-with "$2" 2>/dev/null \
        || "$1" -A OUTPUT -m owner --uid-owner "$range" -j REJECT --reject-with "$2"
}

if ! block_egress iptables icmp-admin-prohibited; then
    echo "XƏTA: şagird uid-ləri ($range) üçün IPv4 şəbəkə bağlanmadı — konteyner başladılmır." >&2
    exit 1
fi
if ! block_egress ip6tables icmp6-adm-prohibited; then
    # IPv6 konteynerdə söndürülübsə (Docker-in defoltu) bağlanacaq bir şey yoxdur; açıqdırsa və qayda qoyulmursa — dayan.
    if [ "$(cat /proc/sys/net/ipv6/conf/all/disable_ipv6 2>/dev/null || echo 1)" = "1" ]; then
        echo "Xəbərdarlıq: ip6tables qaydası qoyulmadı, amma IPv6 konteynerdə söndürülüb." >&2
    else
        echo "XƏTA: şagird uid-ləri ($range) üçün IPv6 şəbəkə bağlanmadı — konteyner başladılmır." >&2
        exit 1
    fi
fi
echo "Şagird kodu (uid $range) üçün şəbəkə çıxışı bağlandı."

# Bounding set-dən atma ardıcıl iki setpriv-lə edilir: setpriv hüquqları nömrə sırası ilə atır (SETPCAP=8 NET_ADMIN=12-dən əvvəl),
# SETPCAP isə özünü atandan sonra başqa hüquq ata bilmir. Birinci addım şəbəkə hüquqlarını atır, ikincisi SETPCAP-ı.
exec setpriv --bounding-set=-net_admin,-net_raw \
    setpriv --bounding-set=-setpcap \
    "$@"
