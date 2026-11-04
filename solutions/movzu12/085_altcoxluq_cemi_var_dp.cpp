#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, t; cin >> n >> t;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    vector<bool> dp(t + 1, false);
    dp[0] = true;
    for (int x : a)
        for (int s = t; s >= x; s--)
            if (dp[s-x]) dp[s] = true;
    cout << (dp[t] ? "VAR" : "YOX") << "\n";
}
