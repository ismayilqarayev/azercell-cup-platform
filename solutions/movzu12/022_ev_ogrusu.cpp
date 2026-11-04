#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<long long> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    vector<long long> dp(n + 1, 0);
    for (int i = 0; i < n; i++) {
        long long take = a[i] + (i >= 1 ? dp[i-1] : 0);
        dp[i+1] = max(dp[i], take);
    }
    cout << dp[n] << "\n";
}
