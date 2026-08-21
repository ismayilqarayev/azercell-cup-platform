#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    bool isPrime = true;   // əvvəlcə n-in sadə olduğunu güman edirik

    // 2-dən √n-ə qədər bölən axtarırıq. Əgər i*i > n olsa, artıq axtarmağa
    // ehtiyac yoxdur (bölənlər cütü olardısa, biri mütləq √n-dən kiçik olardı).
    for (long long i = 2; i * i <= n; i++) {
        if (n % i == 0) {
            isPrime = false;   // bölən tapıldı — sadə deyil
            break;
        }
    }

    cout << (isPrime ? "BELE" : "XEYR") << "\n";
}
