#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
int leafCount = 0;
void dfs(int u, int parent){
    int children = 0;
    for (int v : adj[u]) if (v != parent) { children++; dfs(v, u); }
    if (children == 0) leafCount++;
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
    dfs(s, -1);
    cout << leafCount << "\n";
}
