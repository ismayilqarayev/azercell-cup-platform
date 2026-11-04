#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> dp(n + 1, INT_MAX / 2);
    dp[0] = 0;
    for (int s = 1; s <= n; s++)
        for (int k = 1; k * k <= s; k++)
            dp[s] = min(dp[s], dp[s - k*k] + 1);
    cout << dp[n] << "\n";
}
