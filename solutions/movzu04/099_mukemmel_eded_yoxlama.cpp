#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long sum = 0;
    for (long long i = 1; i * i <= n; i++) {
        if (n % i == 0) {
            if (i != n) sum += i;
            long long other = n / i;
            if (other != i && other != n) sum += other;
        }
    }

    cout << (sum == n ? "BELE" : "XEYR") << "\n";
}
