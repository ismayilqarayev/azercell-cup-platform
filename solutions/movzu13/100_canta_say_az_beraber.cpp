#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, w; cin >> n >> w;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    vector<long long> dp(w + 1, 0);
    dp[0] = 1;
    for (int x : a)
        for (int c = w; c >= x; c--)
            dp[c] += dp[c-x];
    long long total = 0;
    for (int c = 0; c <= w; c++) total += dp[c];
    cout << total << "\n";
}
