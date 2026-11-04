#include <bits/stdc++.h>
using namespace std;
const long long NEG = LLONG_MIN / 2;
int main(){
    int m, n; cin >> m >> n;
    vector<vector<int>> g(m, vector<int>(n));
    for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) cin >> g[i][j];
    vector<vector<long long>> dp(m, vector<long long>(n, NEG));
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++) {
            if (g[i][j] == -1) continue;
            if (i == 0 && j == 0) { dp[i][j] = g[i][j]; continue; }
            long long best = NEG;
            if (i > 0 && dp[i-1][j] > NEG) best = max(best, dp[i-1][j]);
            if (j > 0 && dp[i][j-1] > NEG) best = max(best, dp[i][j-1]);
            if (best > NEG) dp[i][j] = best + g[i][j];
        }
    cout << (dp[m-1][n-1] <= NEG ? -1 : dp[m-1][n-1]) << "\n";
}
