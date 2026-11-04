#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> d(n);
    for (int i = 0; i < n; i++) cin >> d[i];
    sort(d.begin(), d.end());
    vector<bool> used(n + 1, false);
    int cnt = 0;
    for (int dl : d) {
        for (int t = min(dl, n); t >= 1; t--) {
            if (!used[t]) { used[t] = true; cnt++; break; }
        }
    }
    cout << cnt << "\n";
}
