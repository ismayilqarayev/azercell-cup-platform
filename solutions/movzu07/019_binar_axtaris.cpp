#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    int x; cin >> x;
    int pos = lower_bound(a.begin(), a.end(), x) - a.begin();
    if (pos < n && a[pos] == x) cout << (pos + 1) << "\n";
    else cout << -1 << "\n";
}
