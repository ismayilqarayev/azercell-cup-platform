#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    int total = 0;
    for (int i = 0; i < n; i++) { cin >> a[i]; total += a[i]; }
    if (total % 2 != 0) { cout << "YOX" << "\n"; return 0; }
    int half = total / 2;
    vector<bool> dp(half + 1, false);
    dp[0] = true;
    for (int x : a)
        for (int s = half; s >= x; s--)
            if (dp[s-x]) dp[s] = true;
    cout << (dp[half] ? "VAR" : "YOX") << "\n";
}
