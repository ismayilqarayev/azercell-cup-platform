#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    long long x = a, y = b;
    while (y != 0) {
        long long t = x % y;
        x = y;
        y = t;
    }
    long long ebob = x;

    long long ekob = (a / ebob) * b;

    cout << ekob << "\n";
}
