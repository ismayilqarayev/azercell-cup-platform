#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, c, d;
    cin >> a >> b >> c >> d;

    // "Üç ədədin ən böyüyü" ilə eyni fikir, sadəcə bir ədəd də əlavə edirik.
    long long mx = a;
    if (b > mx) mx = b;
    if (c > mx) mx = c;
    if (d > mx) mx = d;

    cout << mx << "\n";
}
