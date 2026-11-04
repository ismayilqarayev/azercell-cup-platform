#include <bits/stdc++.h>
using namespace std;
int n; vector<int> a;
set<long long> sums;
void bt(int idx, long long sum){
    if (idx == n) { sums.insert(sum); return; }
    bt(idx + 1, sum + a[idx]);
    bt(idx + 1, sum);
}
int main(){
    cin >> n;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    bt(0, 0);
    cout << sums.size() << "\n";
}
