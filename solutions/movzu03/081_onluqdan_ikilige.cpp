#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    if (n == 0) { cout << 0 << "\n"; return 0; }

    string bin = "";
    while (n > 0) {
        bin = char('0' + n % 2) + bin;
        n /= 2;
    }

    cout << bin << "\n";
}
