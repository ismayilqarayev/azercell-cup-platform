#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; long long b; cin >> n >> b;
    vector<long long> p(n);
    for (int i = 0; i < n; i++) cin >> p[i];
    sort(p.begin(), p.end());
    int cnt = 0;
    for (long long x : p) {
        if (b >= x) { b -= x; cnt++; }
        else break;
    }
    cout << cnt << "\n";
}
