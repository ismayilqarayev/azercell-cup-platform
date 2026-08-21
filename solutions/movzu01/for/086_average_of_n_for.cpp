#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long s = 0;
    // Bütün ədədləri oxuyub cəmləyirik.
    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        s += x;
    }

    // Orta qiymət = cəm / say. (double)s ilə s-i əvvəlcə kəsr ədədə çeviririk ki,
    // bölmə tam ədəd bölməsi kimi deyil, dəqiq kəsr nəticə versin.
    cout << fixed << setprecision(2) << (double)s / n << "\n";
}
