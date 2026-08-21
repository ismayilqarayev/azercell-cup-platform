#include <bits/stdc++.h>
using namespace std;

bool sadedirmi(long long n){
    if (n < 2) return false;
    for (long long i = 2; i * i <= n; i++) {
        if (n % i == 0) return false;
    }
    return true;
}

int main(){
    long long n; cin >> n;

    cout << (sadedirmi(n) ? "BELE" : "XEYR") << "\n";
}
