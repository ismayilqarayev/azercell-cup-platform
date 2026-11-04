#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<bool> visited;
void dfs(int u){
    visited[u] = true;
    for (int v : adj[u]) if (!visited[v]) dfs(v);
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    int s, t; cin >> s >> t;
    visited.assign(n + 1, false);
    dfs(s);
    cout << (visited[t] ? "VAR" : "YOX") << "\n";
}
