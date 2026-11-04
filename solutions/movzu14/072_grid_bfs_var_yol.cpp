#include <bits/stdc++.h>
using namespace std;
int main(){
    int r, c; cin >> r >> c;
    vector<string> g(r);
    for (int i = 0; i < r; i++) cin >> g[i];
    vector<vector<bool>> visited(r, vector<bool>(c, false));
    if (g[0][0] == '1') { cout << "YOX" << "\n"; return 0; }
    queue<pair<int,int>> q;
    visited[0][0] = true; q.push({0,0});
    int dr[] = {-1,1,0,0}, dc[] = {0,0,-1,1};
    while (!q.empty()) {
        auto [x, y] = q.front(); q.pop();
        for (int d = 0; d < 4; d++) {
            int nx = x + dr[d], ny = y + dc[d];
            if (nx < 0 || nx >= r || ny < 0 || ny >= c) continue;
            if (g[nx][ny] == '1' || visited[nx][ny]) continue;
            visited[nx][ny] = true;
            q.push({nx, ny});
        }
    }
    cout << (visited[r-1][c-1] ? "VAR" : "YOX") << "\n";
}
