#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    bool first = true;
    for (long long i = 2; i * i <= n; i++) {
        while (n % i == 0) {
            if (!first) cout << " ";
            cout << i;
            first = false;
            n /= i;
        }
    }
    if (n > 1) {
        if (!first) cout << " ";
        cout << n;
    }
    cout << "\n";
}
