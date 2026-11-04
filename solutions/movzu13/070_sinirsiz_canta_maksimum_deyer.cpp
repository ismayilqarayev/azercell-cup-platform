#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, w; cin >> n >> w;
    vector<int> wt(n), val(n);
    for (int i = 0; i < n; i++) cin >> wt[i] >> val[i];
    vector<long long> dp(w + 1, 0);
    for (int c = 1; c <= w; c++)
        for (int i = 0; i < n; i++)
            if (wt[i] <= c) dp[c] = max(dp[c], dp[c - wt[i]] + val[i]);
    cout << dp[w] << "\n";
}
