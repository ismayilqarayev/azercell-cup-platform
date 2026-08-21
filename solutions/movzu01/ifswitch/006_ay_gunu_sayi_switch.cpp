#include <bits/stdc++.h>
using namespace std;

int main(){
    int ay;
    cin >> ay;

    int gun;
    // switch operatoru dəyişənin dəyərinə görə uyğun "case"-ə tullanır.
    switch (ay) {
        case 2:
            gun = 28;   // fevral (adi ildə)
            break;      // break olmasa, aşağıdakı case-lərə də "düşərdi" (fallthrough)
        case 4: case 6: case 9: case 11:
            // bu 4 ay üçün eyni nəticə lazımdır, ona görə case-ləri
            // ard-arda yazıb aralarında break QOYMURUQ — hamısı eyni koda düşür
            gun = 30;
            break;
        default:
            // qalan bütün aylar (1,3,5,7,8,10,12) 31 gündür
            gun = 31;
    }

    cout << gun << "\n";
}
