#include <bits/stdc++.h>
using namespace std;
int n, k; vector<int> a;
int best = 0;
void bt(int idx, int lastVal, int len){
    best = max(best, len);
    if (idx == n) return;
    if (a[idx] > lastVal) bt(idx + 1, a[idx], len + 1);
    bt(idx + 1, lastVal, len);
}
int main(){
    cin >> n >> k;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    bt(0, INT_MIN, 0);
    cout << (best >= k ? "VAR" : "YOX") << "\n";
}
