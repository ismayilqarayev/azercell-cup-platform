#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    // Xüsusi hal: 0 ədədinin özü 1 rəqəmdən ibarətdir, amma aşağıdakı dövr
    // 0 üçün heç işləməzdi (çünki n>0 şərti dərhal yalan olardı) — ona görə
    // bunu əvvəlcədən ayrıca yoxlayırıq.
    if (n == 0) {
        cout << 1 << "\n";
        return 0;
    }

    int cnt = 0;
    // Hər addımda n-i 10-a bölərək bir rəqəmi "atırıq" və sayğacı artırırıq —
    // dövr neçə dəfə işləsə, ədəddə o qədər rəqəm var demişdir.
    while (n > 0) {
        cnt++;
        n /= 10;
    }

    cout << cnt << "\n";
}
