#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    long long mx;
    cin >> mx;
    for (int i = 1; i < n; i++) { long long x; cin >> x; if (x > mx) mx = x; }
    cout << mx << "\n";
}
