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
    vector<int> dist(n + 1, -1);
    queue<int> q;
    dist[s] = 0; q.push(s);
    int maxD = 0;
    while (!q.empty()) {
        int u = q.front(); q.pop();
        for (int v : adj[u]) if (dist[v] == -1) { dist[v] = dist[u] + 1; maxD = max(maxD, dist[v]); q.push(v); }
    }
    cout << (maxD + 1) << "\n";
}
