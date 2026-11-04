#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, m; cin >> n >> m;
    vector<int> deg(n + 1, 0);
    for (int i = 0; i < m; i++) { int u, v; cin >> u >> v; deg[u]++; deg[v]++; }
    int best = 1;
    for (int i = 2; i <= n; i++) if (deg[i] > deg[best]) best = i;
    cout << best << "\n";
}
