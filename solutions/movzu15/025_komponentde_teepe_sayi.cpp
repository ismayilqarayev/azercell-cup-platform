#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<bool> visited;
int dfs(int u){
    visited[u] = true;
    int size = 1;
    for (int v : adj[u]) if (!visited[v]) size += dfs(v);
    return size;
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    int vtx; cin >> vtx;
    visited.assign(n + 1, false);
    cout << dfs(vtx) << "\n";
}
