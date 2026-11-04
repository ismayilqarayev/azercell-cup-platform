#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<int> state; // 0=ziyarət edilməyib, 1=cari yolda, 2=tamamlanıb
bool hasCycle;
void dfs(int u){
    state[u] = 1;
    for (int v : adj[u]) {
        if (state[v] == 1) hasCycle = true;
        else if (state[v] == 0) dfs(v);
    }
    state[u] = 2;
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) { int u, v; cin >> u >> v; adj[u].push_back(v); }
    state.assign(n + 1, 0);
    hasCycle = false;
    for (int i = 1; i <= n; i++) if (state[i] == 0) dfs(i);
    cout << (hasCycle ? "VAR" : "YOX") << "\n";
}
