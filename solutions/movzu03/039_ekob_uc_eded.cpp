#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, c;
    cin >> a >> b >> c;

    long long x = a, y = b;
    while (y != 0) { long long t = x % y; x = y; y = t; }
    long long ekob1 = (a / x) * b;

    x = ekob1; y = c;
    while (y != 0) { long long t = x % y; x = y; y = t; }
    long long ekob2 = (ekob1 / x) * c;

    cout << ekob2 << "\n";
}
