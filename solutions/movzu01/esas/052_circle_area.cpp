#include <bits/stdc++.h>
using namespace std;

int main(){
    double r;                  // Dairənin radiusu (kəsr ədəd ola bilər)
    cin >> r;

    double pi = 3.14159;        // Pi ədədinin təxmini dəyəri

    // Dairənin sahə düsturu: pi * radius * radius.
    // fixed və setprecision(2) nəticəni tam olaraq 2 rəqəmdən sonra vergüllə göstərir
    // (məsələn 78.5398... əvəzinə 78.54 çap olunur).
    cout << fixed << setprecision(2) << pi * r * r << "\n";
}
