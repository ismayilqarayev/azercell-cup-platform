#include <bits/stdc++.h>
using namespace std;

int main(){
    int p1, p2;   // 1=daş, 2=kağız, 3=qayçı
    cin >> p1 >> p2;

    // Eyni seçimdirsə heç-heçədir.
    if (p1 == p2) {
        cout << "HEC-HECE" << "\n";
        return 0;
    }

    // Qalib olan 3 kombinasiyanı yoxlayırıq: daş qayçını əzir,
    // qayçı kağızı kəsir, kağız daşı bükür. Bunlardan biri p1 üçün doğrudursa,
    // 1-ci oyunçu udur, əks halda udan 2-ci oyunçudur.
    if ((p1 == 1 && p2 == 3) || (p1 == 3 && p2 == 2) || (p1 == 2 && p2 == 1))
        cout << "1-CI OYUNCU" << "\n";
    else
        cout << "2-CI OYUNCU" << "\n";
}
