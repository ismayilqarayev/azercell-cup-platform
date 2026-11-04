#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    string r = s; reverse(r.begin(), r.end());
    int n = s.size();
    vector<vector<int>> dp(n+1, vector<int>(n+1, 0));
    for (int i = 1; i <= n; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = (s[i-1] == r[j-1]) ? dp[i-1][j-1] + 1 : max(dp[i-1][j], dp[i][j-1]);
    cout << dp[n][n] << "\n";
}
