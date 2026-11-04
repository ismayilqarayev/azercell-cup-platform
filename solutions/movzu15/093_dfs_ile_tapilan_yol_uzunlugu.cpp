#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<bool> visited;
int target;
int dfs(int u, int len){
    if (u == target) return len;
    visited[u] = true;
    for (int v : adj[u]) {
        if (!visited[v]) {
            int r = dfs(v, len + 1);
            if (r != -1) return r;
        }
    }
    return -1;
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    for (int i = 1; i <= n; i++) sort(adj[i].begin(), adj[i].end());
    int s, t; cin >> s >> t;
    target = t;
    visited.assign(n + 1, false);
    cout << dfs(s, 0) << "\n";
}
