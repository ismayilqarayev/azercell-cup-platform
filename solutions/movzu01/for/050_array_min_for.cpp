#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    long long mn;
    cin >> mn;
    for (int i = 1; i < n; i++) { long long x; cin >> x; if (x < mn) mn = x; }
    cout << mn << "\n";
}
