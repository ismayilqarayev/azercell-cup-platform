#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<bool> visited;
bool hasCycle;
void dfs(int u, int parent){
    visited[u] = true;
    for (int v : adj[u]) {
        if (!visited[v]) dfs(v, u);
        else if (v != parent) hasCycle = true;
    }
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    visited.assign(n + 1, false);
    hasCycle = false;
    for (int i = 1; i <= n; i++) if (!visited[i]) dfs(i, -1);
    cout << (hasCycle ? "VAR" : "YOX") << "\n";
}
