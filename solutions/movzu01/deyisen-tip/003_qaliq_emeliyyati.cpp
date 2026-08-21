#include <bits/stdc++.h>
using namespace std;

int main(){
    int a, b;
    cin >> a >> b;

    // % (qalıq) operatoru C++-da bölünəndən (a-dan) asılı işarə verir —
    // riyaziyyatda öyrəndiyimiz "mod"dan fərqli ola bilər.
    // Məsələn -7 % 3: riyazi olaraq 2 gözlənilsə də, C++-da -1 nəticəsi çıxır.
    cout << a % b << "\n";
}
