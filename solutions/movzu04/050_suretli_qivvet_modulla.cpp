#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, m;
    cin >> a >> b >> m;

    long long res = 1;
    long long baza = a % m;
    while (b > 0) {
        if (b & 1) {
            res = res * baza % m;
        }
        baza = baza * baza % m;
        b >>= 1;
    }

    cout << res << "\n";
}
