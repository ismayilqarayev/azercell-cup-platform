#include <bits/stdc++.h>
using namespace std;
int n; vector<int> a;
vector<bool> used;
long long cnt = 0;
void bt(int depth){
    if (depth == n) { cnt++; return; }
    for (int i = 0; i < n; i++) {
        if (used[i]) continue;
        if (i > 0 && a[i] == a[i-1] && !used[i-1]) continue;
        used[i] = true;
        bt(depth + 1);
        used[i] = false;
    }
}
int main(){
    cin >> n;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    sort(a.begin(), a.end());
    used.assign(n, false);
    bt(0);
    cout << cnt << "\n";
}
