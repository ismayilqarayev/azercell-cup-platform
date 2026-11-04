#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, m; cin >> n >> m;
    vector<vector<int>> adj(n + 1);
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    for (int i = 1; i <= n; i++) sort(adj[i].begin(), adj[i].end());
    int s; cin >> s;
    vector<bool> visited(n + 1, false);
    queue<int> q;
    q.push(s); visited[s] = true;
    vector<int> order;
    while (!q.empty()) {
        int u = q.front(); q.pop();
        order.push_back(u);
        for (int v : adj[u]) if (!visited[v]) { visited[v] = true; q.push(v); }
    }
    for (size_t i = 0; i < order.size(); i++) cout << order[i] << (i + 1 < order.size() ? " " : "\n");
}
