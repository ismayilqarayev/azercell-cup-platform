#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, m; cin >> n >> m;
    vector<int> deg(n + 1, 0);
    for (int i = 0; i < m; i++) { int u, v; cin >> u >> v; deg[u]++; deg[v]++; }
    int cnt = 0;
    for (int i = 1; i <= n; i++) if (deg[i] == 0) cnt++;
    cout << cnt << "\n";
}
