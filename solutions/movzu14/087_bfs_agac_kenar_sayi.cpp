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
    int s; cin >> s;
    vector<bool> visited(n + 1, false);
    queue<int> q;
    q.push(s); visited[s] = true;
    int reached = 1;
    while (!q.empty()) {
        int u = q.front(); q.pop();
        for (int v : adj[u]) if (!visited[v]) { visited[v] = true; reached++; q.push(v); }
    }
    cout << (reached - 1) << "\n";
}
