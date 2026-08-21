#include <bits/stdc++.h>
using namespace std;

int main(){
    int y;                     // il
    cin >> y;

    // Uzun (keçən) il qaydası: il 4-ə bölünməlidir, AMMA əgər həm də
    // 100-ə bölünürsə, onda 400-ə də bölünməlidir ki, uzun il sayılsın.
    // (məs. 2000 uzun ildir, 1900 isə deyil, çünki 1900 100-ə bölünür,
    // 400-ə isə bölünmür)
    bool leap = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0);

    cout << (leap ? "BELE" : "XEYR") << "\n";
}
