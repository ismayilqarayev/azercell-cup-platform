#include <bits/stdc++.h>
using namespace std;

int main(){
    int bal;                  // Şagirdin balı (0-100 arası)
    cin >> bal;

    // DİQQƏT: şərtləri həmişə ƏN BÖYÜK həddən başlayaraq yoxlamaq lazımdır!
    // Əgər əvvəlcə "bal >= 50" yoxlansaydı, 95 balı olan şagird də səhvən
    // "C" alardı, çünki 95 həm də >= 50-dir. Ona görə böyükdən kiçiyə gedirik.
    if (bal >= 90)
        cout << "A" << "\n";
    else if (bal >= 70)
        cout << "B" << "\n";
    else if (bal >= 50)
        cout << "C" << "\n";
    else
        cout << "D" << "\n";
}
