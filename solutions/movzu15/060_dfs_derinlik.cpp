#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<bool> visited;
int dfs(int u){
    visited[u] = true;
    int best = 0;
    for (int v : adj[u]) if (!visited[v]) best = max(best, 1 + dfs(v));
    return best;
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    int s; cin >> s;
    visited.assign(n + 1, false);
    cout << dfs(s) << "\n";
}
