#include <bits/stdc++.h>
using namespace std;
int main(){
    int m, n; cin >> m >> n;
    vector<string> g(m);
    for (int i = 0; i < m; i++) cin >> g[i];
    vector<vector<long long>> dp(m, vector<long long>(n, 0));
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++) {
            if (g[i][j] == '1') { dp[i][j] = 0; continue; }
            if (i == 0 && j == 0) dp[i][j] = 1;
            else {
                long long up = (i > 0) ? dp[i-1][j] : 0;
                long long left = (j > 0) ? dp[i][j-1] : 0;
                dp[i][j] = up + left;
            }
        }
    cout << dp[m-1][n-1] << "\n";
}
