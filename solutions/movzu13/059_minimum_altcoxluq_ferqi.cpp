#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    int total = 0;
    for (int i = 0; i < n; i++) { cin >> a[i]; total += a[i]; }
    vector<bool> dp(total + 1, false);
    dp[0] = true;
    for (int x : a)
        for (int s = total; s >= x; s--)
            if (dp[s-x]) dp[s] = true;
    int best = total;
    for (int s = 0; s <= total / 2; s++)
        if (dp[s]) best = min(best, total - 2 * s);
    cout << best << "\n";
}
