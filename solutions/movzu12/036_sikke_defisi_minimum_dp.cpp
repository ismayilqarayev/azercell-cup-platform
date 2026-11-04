#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, t; cin >> n >> t;
    vector<int> c(n);
    for (int i = 0; i < n; i++) cin >> c[i];
    vector<int> dp(t + 1, INT_MAX / 2);
    dp[0] = 0;
    for (int s = 1; s <= t; s++)
        for (int x : c)
            if (x <= s) dp[s] = min(dp[s], dp[s-x] + 1);
    cout << (dp[t] >= INT_MAX / 2 ? -1 : dp[t]) << "\n";
}
