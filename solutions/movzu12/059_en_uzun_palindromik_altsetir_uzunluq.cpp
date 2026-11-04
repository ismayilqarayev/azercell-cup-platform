#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    int n = s.size();
    vector<vector<bool>> dp(n, vector<bool>(n, false));
    int best = 1;
    for (int i = 0; i < n; i++) dp[i][i] = true;
    for (int len = 2; len <= n; len++) {
        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            if (s[l] == s[r] && (len == 2 || dp[l+1][r-1])) {
                dp[l][r] = true;
                best = max(best, len);
            }
        }
    }
    cout << best << "\n";
}
