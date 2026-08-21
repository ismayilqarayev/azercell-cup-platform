#include <bits/stdc++.h>
using namespace std;

int main(){
    char c;
    int k;
    cin >> c >> k;

    // 'a'-'z' hərflərini 0-25 ədədlərinə uyğunlaşdırmaq üçün hərfdən 'a'-nı
    // çıxırıq (c - 'a'). K əlavə edib, 26-ya görə qalıq alırıq ki, z-dən
    // sonra yenidən a-ya "dövrə vuraq" (dairəvi sürüşdürmə).
    int idx = (c - 'a' + k) % 26;

    // İndeksi yenidən hərfə çeviririk.
    cout << (char)('a' + idx) << "\n";
}
