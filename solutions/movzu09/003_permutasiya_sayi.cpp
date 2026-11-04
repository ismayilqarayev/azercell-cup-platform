#include <bits/stdc++.h>
using namespace std;
int n;
vector<bool> used;
long long cnt = 0;
void bt(int depth){
    if (depth == n) { cnt++; return; }
    for (int i = 0; i < n; i++) {
        if (used[i]) continue;
        used[i] = true;
        bt(depth + 1);
        used[i] = false;
    }
}
int main(){
    cin >> n;
    used.assign(n, false);
    bt(0);
    cout << cnt << "\n";
}
