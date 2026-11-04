#include <bits/stdc++.h>
using namespace std;
int n; vector<int> a;
long long cnt = 0;
void bt(int idx, int remain){
    if (remain == 0) { cnt++; return; }
    if (idx == n || remain < 0) return;
    bt(idx + 1, remain - a[idx]);
    bt(idx + 1, remain);
}
int main(){
    int t; cin >> n >> t;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    bt(0, t);
    cout << cnt << "\n";
}
