#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, m; cin >> n >> m;
    vector<vector<int>> adj(n + 1);
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
    }
    int s, t; cin >> s >> t;
    vector<bool> visited(n + 1, false);
    queue<int> q;
    q.push(s); visited[s] = true;
    while (!q.empty()) {
        int u = q.front(); q.pop();
        for (int v : adj[u]) if (!visited[v]) { visited[v] = true; q.push(v); }
    }
    cout << (visited[t] ? "VAR" : "YOX") << "\n";
}
