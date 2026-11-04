#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, w, k; cin >> n >> w >> k;
    vector<int> wt(n), val(n);
    for (int i = 0; i < n; i++) cin >> wt[i] >> val[i];
    vector<vector<long long>> dp(w + 1, vector<long long>(k + 1, LLONG_MIN / 2));
    dp[0][0] = 0;
    for (int i = 0; i < n; i++)
        for (int c = w; c >= wt[i]; c--)
            for (int kk = k; kk >= 1; kk--)
                dp[c][kk] = max(dp[c][kk], dp[c - wt[i]][kk - 1] + val[i]);
    cout << (dp[w][k] < 0 ? -1 : dp[w][k]) << "\n";
}
