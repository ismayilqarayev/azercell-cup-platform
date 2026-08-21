#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, c;
    cin >> a >> b >> c;

    long long x = a, y = b;
    while (y != 0) {
        long long t = x % y;
        x = y;
        y = t;
    }
    long long ebob1 = x;

    x = ebob1; y = c;
    while (y != 0) {
        long long t = x % y;
        x = y;
        y = t;
    }

    cout << x << "\n";
}
