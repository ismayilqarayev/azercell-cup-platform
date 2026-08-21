#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    long long res = 1;
    long long baza = a;
    while (b > 0) {
        if (b & 1) {
            res *= baza;
        }
        baza *= baza;
        b >>= 1;
    }

    cout << res << "\n";
}
