#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n, k;
    cin >> n >> k;

    long long s = 0;
    while (n > 0) {
        s += n % 10;
        n /= 10;
    }

    cout << (s % k == 0 ? "BELE" : "XEYR") << "\n";
}
