#include <bits/stdc++.h>
using namespace std;
vector<vector<int>> adj;
vector<int> color;
void dfs(int u, int c){
    color[u] = c;
    for (int v : adj[u]) if (color[v] == -1) dfs(v, 1 - c);
}
int main(){
    int n, m; cin >> n >> m;
    adj.assign(n + 1, {});
    for (int i = 0; i < m; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    color.assign(n + 1, -1);
    for (int i = 1; i <= n; i++) if (color[i] == -1) dfs(i, 0);
    int cnt = 0;
    for (int i = 1; i <= n; i++) if (color[i] == 0) cnt++;
    cout << cnt << "\n";
}
