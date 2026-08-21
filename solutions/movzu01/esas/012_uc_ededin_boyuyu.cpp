#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b, c;
    cin >> a >> b >> c;
    long long mx = a;
    if (b > mx) mx = b;
    if (c > mx) mx = c;
    cout << mx << "\n";
}
