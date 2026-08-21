#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long s = 0;   // cəmi yığacağımız dəyişən, əvvəlcə 0

    // for dövrü: i dəyişəni 1-dən başlayır, hər addımda 1 artır, n-ə çatanda dayanır.
    // Hər addımda cari i dəyərini cəmə əlavə edirik.
    for (int i = 1; i <= n; i++) {
        s += i;   // s = s + i ilə eynidir
    }

    cout << s << "\n";
}
