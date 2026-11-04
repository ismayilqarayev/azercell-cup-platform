#include <bits/stdc++.h>
using namespace std;
int main(){
    int m, n; cin >> m >> n;
    vector<string> g(m);
    for (int i = 0; i < m; i++) cin >> g[i];
    vector<vector<int>> dp(m, vector<int>(n, 0));
    int best = 0;
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++) {
            if (g[i][j] == '1') {
                if (i == 0 || j == 0) dp[i][j] = 1;
                else dp[i][j] = 1 + min({dp[i-1][j], dp[i][j-1], dp[i-1][j-1]});
                best = max(best, dp[i][j]);
            }
        }
    cout << best << "\n";
}
