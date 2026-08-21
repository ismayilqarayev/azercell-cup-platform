#include <bits/stdc++.h>
using namespace std;

int main(){
    long long x, y;
    cin >> x >> y;

    // Əgər x və ya y sıfırdırsa, nöqtə koordinat oxlarının biri üzərindədir
    // (heç bir kvadranta aid deyil) — bunu əvvəlcədən ayırırıq.
    if (x == 0 || y == 0) {
        cout << "OX_UZERINDE" << "\n";
        return 0;
    }

    // Qalan hallarda x və y-nin işarələrinin (müsbət/mənfi) kombinasiyası
    // hansı kvadrantda olduğumuzu göstərir.
    if (x > 0 && y > 0) cout << "I" << "\n";
    else if (x < 0 && y > 0) cout << "II" << "\n";
    else if (x < 0 && y < 0) cout << "III" << "\n";
    else cout << "IV" << "\n";
}
