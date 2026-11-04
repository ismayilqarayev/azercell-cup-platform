#include <bits/stdc++.h>
using namespace std;
int main(){
    int m, n; cin >> m >> n;
    vector<vector<long long>> g(m, vector<long long>(n));
    for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) cin >> g[i][j];
    vector<vector<long long>> dp(m, vector<long long>(n));
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++) {
            if (i == 0 && j == 0) dp[i][j] = g[i][j];
            else if (i == 0) dp[i][j] = dp[i][j-1] + g[i][j];
            else if (j == 0) dp[i][j] = dp[i-1][j] + g[i][j];
            else dp[i][j] = g[i][j] + min(dp[i-1][j], dp[i][j-1]);
        }
    cout << dp[m-1][n-1] << "\n";
}
