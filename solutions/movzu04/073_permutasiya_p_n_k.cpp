#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n, k;
    cin >> n >> k;

    long long res = 1;
    for (long long i = 0; i < k; i++) {
        res *= (n - i);
    }

    cout << res << "\n";
}
