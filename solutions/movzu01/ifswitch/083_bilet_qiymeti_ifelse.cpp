#include <bits/stdc++.h>
using namespace std;

int main(){
    int yas;
    cin >> yas;

    int baza = 20;   // əsas (tam) bilet qiyməti

    // Yaş qrupuna görə fərqli qiymət tətbiq edirik.
    if (yas < 6)
        cout << 0 << "\n";                    // 6 yaşdan kiçiklər pulsuz
    else if (yas <= 17)
        cout << baza * 50 / 100 << "\n";        // 50% endirim
    else if (yas <= 64)
        cout << baza << "\n";                   // tam qiymət
    else
        cout << baza * 70 / 100 << "\n";         // 65+ yaş: 30% endirim (qiymətin 70%-i qalır)
}
