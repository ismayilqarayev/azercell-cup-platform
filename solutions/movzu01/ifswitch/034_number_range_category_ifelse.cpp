#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    // Ən dar (kiçik) diapazondan başlayaraq geniş diapazona doğru yoxlayırıq.
    if (n < 0)
        cout << "MENFI" << "\n";
    else if (n == 0)
        cout << "SIFIR" << "\n";
    else if (n <= 10)
        cout << "KICIK" << "\n";
    else if (n <= 100)
        cout << "ORTA" << "\n";
    else
        cout << "BOYUK" << "\n";
}
