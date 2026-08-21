#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    long long res = 1;
    long long baza = a % 10;
    while (b > 0) {
        if (b & 1) {
            res = res * baza % 10;
        }
        baza = baza * baza % 10;
        b >>= 1;
    }

    cout << res << "\n";
}
