#include <bits/stdc++.h>
using namespace std;

int main(){
    char c;
    cin >> c;

    // isdigit, isupper, islower — C++-ın hazır (kitabxanadan gələn) sual
    // funksiyalarıdır: "bu simvol rəqəmdirmi?", "böyük hərfdirmi?" və s.
    // (unsigned char)-a çevirmək kiçik texniki tələbdir ki, funksiyalar düzgün işləsin.
    if (isdigit((unsigned char)c))
        cout << "REQEM" << "\n";
    else if (isupper((unsigned char)c))
        cout << "BOYUK_HERF" << "\n";
    else if (islower((unsigned char)c))
        cout << "KICIK_HERF" << "\n";
    else
        cout << "DIGER" << "\n";
}
