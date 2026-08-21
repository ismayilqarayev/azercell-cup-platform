#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    int mx = 0;
    while (n > 0) {
        int d = n % 10;
        if (d > mx) mx = d;
        n /= 10;
    }

    cout << mx << "\n";
}
