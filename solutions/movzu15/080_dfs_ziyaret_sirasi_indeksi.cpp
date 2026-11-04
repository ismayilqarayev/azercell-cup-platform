#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<int> order_idx;
int counter_ = 0;
void dfs(int u){
    order_idx[u] = ++counter_;
    for (int v : adj[u]) if (order_idx[v] == 0) dfs(v);
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
    order_idx.assign(n + 1, 0);
    dfs(s);
    cout << (order_idx[t] == 0 ? -1 : order_idx[t]) << "\n";
}
