#include <bits/stdc++.h>
using namespace std;
int n; vector<int> a;
bool bt(int idx, int remain){
    if (remain == 0) return true;
    if (idx == n || remain < 0) return false;
    if (bt(idx + 1, remain - a[idx])) return true;
    return bt(idx + 1, remain);
}
int main(){
    int t; cin >> n >> t;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    cout << (bt(0, t) ? "VAR" : "YOX") << "\n";
}
