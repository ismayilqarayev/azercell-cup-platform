#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<long long> cost(n);
    for (int i = 0; i < n; i++) cin >> cost[i];
    vector<long long> dp(n);
    dp[0] = cost[0];
    if (n > 1) dp[1] = cost[1];
    for (int i = 2; i < n; i++) dp[i] = cost[i] + min(dp[i-1], dp[i-2]);
    if (n == 1) cout << dp[0] << "\n";
    else cout << min(dp[n-1], dp[n-2]) << "\n";
}
