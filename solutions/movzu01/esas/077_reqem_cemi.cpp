#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long s = 0;           // rəqəmlərin cəmini yığacağımız dəyişən

    // Hər addımda: n%10 ilə n-in ən sonuncu rəqəmini "qoparırıq",
    // onu cəmə əlavə edirik, sonra n/=10 ilə həmin rəqəmi atırıq.
    // Məsələn n=123: 1-ci addımda 3 alınır (s=3, n=12),
    // 2-ci addımda 2 alınır (s=5, n=1), 3-cü addımda 1 alınır (s=6, n=0) → dövr bitir.
    while (n > 0) {
        s += n % 10;
        n /= 10;
    }

    cout << s << "\n";
}
