#include <bits/stdc++.h>
using namespace std;
int n; vector<long long> a;
long long best = LLONG_MIN;
void bt(int idx, long long prod, bool any){
    if (idx == n) { if (any) best = max(best, prod); return; }
    bt(idx + 1, prod * a[idx], true); // daxil et
    bt(idx + 1, prod, any);           // keç
}
int main(){
    cin >> n;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    bt(0, 1, false);
    cout << best << "\n";
}
