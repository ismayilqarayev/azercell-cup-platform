#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, t; cin >> n >> t;
    vector<int> c(n);
    for (int i = 0; i < n; i++) cin >> c[i];
    vector<long long> dp(t + 1, 0);
    dp[0] = 1;
    for (int x : c)
        for (int s = x; s <= t; s++)
            dp[s] += dp[s-x];
    cout << dp[t] << "\n";
}
