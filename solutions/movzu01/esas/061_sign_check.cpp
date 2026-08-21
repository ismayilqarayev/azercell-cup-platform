#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    // Əvvəlcə ən "xüsusi" halı — sıfırı — yoxlayırıq, çünki sıfır nə
    // müsbətdir, nə mənfi.
    if (n == 0)
        cout << "SIFIR" << "\n";
    else if (n > 0)
        cout << "MUSBET" << "\n";
    else
        cout << "MENFI" << "\n";   // buraya çatıbsa, n sıfırdan da, müsbətdən də başqadır
}
