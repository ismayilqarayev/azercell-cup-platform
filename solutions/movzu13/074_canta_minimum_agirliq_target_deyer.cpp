#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, t; cin >> n >> t;
    vector<int> wt(n), val(n);
    int maxV = 0;
    for (int i = 0; i < n; i++) { cin >> wt[i] >> val[i]; maxV += val[i]; }
    vector<long long> dp(maxV + 1, LLONG_MAX / 2);
    dp[0] = 0;
    for (int i = 0; i < n; i++)
        for (int v = maxV; v >= val[i]; v--)
            dp[v] = min(dp[v], dp[v - val[i]] + wt[i]);
    long long best = LLONG_MAX / 2;
    for (int v = t; v <= maxV; v++) best = min(best, dp[v]);
    cout << (best >= LLONG_MAX / 2 ? -1 : best) << "\n";
}
