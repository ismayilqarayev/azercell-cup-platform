#include <bits/stdc++.h>
using namespace std;
int n; long long t; vector<int> a;
long long cnt = 0;
void bt(int idx, long long sum){
    if (idx == n) { if (sum == t) cnt++; return; }
    bt(idx + 1, sum + a[idx]);
    bt(idx + 1, sum - a[idx]);
}
int main(){
    cin >> n >> t;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    bt(0, 0);
    cout << cnt << "\n";
}
