#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, m; cin >> n >> m;
    vector<int> indeg(n + 1, 0);
    for (int i = 0; i < m; i++) { int u, v; cin >> u >> v; indeg[v]++; }
    int vtx; cin >> vtx;
    cout << indeg[vtx] << "\n";
}
