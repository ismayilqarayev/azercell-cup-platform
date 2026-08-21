#include <bits/stdc++.h>
using namespace std;
int main(){
    long long n; cin >> n;
    bool isPrime = true;
    for (long long i = 2; i * i <= n; i++) {
        if (n % i == 0) { isPrime = false; break; }
    }
    cout << (isPrime ? "BELE" : "XEYR") << "\n";
}
