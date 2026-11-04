#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    int reach = 0;
    bool ok = true;
    for (int i = 0; i < n; i++) {
        if (i > reach) { ok = false; break; }
        reach = max(reach, i + a[i]);
    }
    cout << (ok ? "VAR" : "YOX") << "\n";
}
