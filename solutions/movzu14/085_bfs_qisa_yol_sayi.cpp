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
    int s, t; cin >> s >> t;
    vector<int> dist(n + 1, -1);
    vector<long long> ways(n + 1, 0);
    queue<int> q;
    dist[s] = 0; ways[s] = 1; q.push(s);
    while (!q.empty()) {
        int u = q.front(); q.pop();
        for (int v : adj[u]) {
            if (dist[v] == -1) { dist[v] = dist[u] + 1; ways[v] = ways[u]; q.push(v); }
            else if (dist[v] == dist[u] + 1) { ways[v] += ways[u]; }
        }
    }
    cout << ways[t] << "\n";
}
