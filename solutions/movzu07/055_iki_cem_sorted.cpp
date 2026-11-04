#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    int t; cin >> t;
    int lo = 0, hi = n - 1;
    bool found = false;
    while (lo < hi) {
        int s = a[lo] + a[hi];
        if (s == t) { found = true; break; }
        else if (s < t) lo++;
        else hi--;
    }
    cout << (found ? "VAR" : "YOX") << "\n";
}
