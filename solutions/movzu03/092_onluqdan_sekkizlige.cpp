#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    if (n == 0) { cout << 0 << "\n"; return 0; }

    string oct8 = "";
    while (n > 0) {
        oct8 = char('0' + n % 8) + oct8;
        n /= 8;
    }

    cout << oct8 << "\n";
}
