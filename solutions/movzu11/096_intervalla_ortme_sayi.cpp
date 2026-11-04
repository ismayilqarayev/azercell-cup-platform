#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, t; cin >> n >> t;
    vector<pair<int,int>> a(n);
    for (int i = 0; i < n; i++) cin >> a[i].first >> a[i].second;
    sort(a.begin(), a.end());
    int covered = 0, cnt = 0, idx = 0;
    while (covered < t) {
        int best = covered;
        while (idx < n && a[idx].first <= covered) { best = max(best, a[idx].second); idx++; }
        covered = best;
        cnt++;
    }
    cout << cnt << "\n";
}
