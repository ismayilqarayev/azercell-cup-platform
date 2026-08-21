#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n, k;
    cin >> n >> k;

    long long res = 1;
    for (long long i = 1; i <= k; i++) {
        res = res * (n - k + i) / i;
    }

    cout << res << "\n";
}
