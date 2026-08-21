#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // ƏBOB (ən böyük ortaq bölən) tapmaq üçün Evklid alqoritmindən istifadə
    // edirik: (a, b) cütünü b sıfır olana qədər (b, a mod b) ilə əvəz edirik.
    // Hər addımda a%b hesablanır, sonra a b-nin yerinə, b isə həmin qalığın
    // yerinə keçir. b sıfır olanda, a-da qalan dəyər ƏBOB-dur.
    while (b != 0) {
        long long t = a % b;   // qalığı müvəqqəti saxlayırıq
        a = b;                 // b-ni a-ya keçiririk
        b = t;                 // qalığı yeni b edirik
    }

    cout << a << "\n";
}
