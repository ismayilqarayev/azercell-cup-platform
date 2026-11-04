#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, k; cin >> n >> k;
    vector<long long> dp(n + 1, 0);
    dp[0] = 1;
    for (int i = 1; i <= n; i++)
        for (int s = 1; s <= k && s <= i; s++)
            dp[i] += dp[i-s];
    cout << dp[n] << "\n";
}
