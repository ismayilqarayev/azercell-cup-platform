#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<int> comp;
void dfs(int u, int c){
    comp[u] = c;
    for (int v : adj[u]) if (comp[v] == 0) dfs(v, c);
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    int vtx; cin >> vtx;
    comp.assign(n + 1, 0);
    int c = 0;
    for (int i = 1; i <= n; i++) if (comp[i] == 0) { c++; dfs(i, c); }
    cout << comp[vtx] << "\n";
}
