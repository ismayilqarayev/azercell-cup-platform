#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    bool isPrime = true;
    if (n < 2) isPrime = false;
    for (long long i = 2; i * i <= n && isPrime; i++) {
        if (n % i == 0) isPrime = false;
    }

    cout << (isPrime ? "BELE" : "XEYR") << "\n";
}
