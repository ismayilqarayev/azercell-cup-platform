#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<long long> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    vector<long long> dp(n);
    dp[0] = a[0];
    if (n > 1) dp[1] = a[0] + a[1];
    for (int i = 2; i < n; i++) dp[i] = a[i] + max(dp[i-1], dp[i-2]);
    cout << dp[n-1] << "\n";
}
