#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<bool> visited;
vector<int> order;
void dfs(int u){
    visited[u] = true;
    order.push_back(u);
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
    for (int i = 1; i <= n; i++) sort(adj[i].begin(), adj[i].end());
    int s; cin >> s;
    visited.assign(n + 1, false);
    dfs(s);
    for (size_t i = 0; i < order.size(); i++) cout << order[i] << (i + 1 < order.size() ? " " : "\n");
}
