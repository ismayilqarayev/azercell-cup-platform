#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, w; cin >> n >> w;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    vector<bool> dp(w + 1, false);
    dp[0] = true;
    for (int x : a)
        for (int c = w; c >= x; c--)
            if (dp[c-x]) dp[c] = true;
    cout << (dp[w] ? "VAR" : "YOX") << "\n";
}
