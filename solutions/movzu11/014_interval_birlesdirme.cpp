#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<pair<int,int>> a(n);
    for (int i = 0; i < n; i++) cin >> a[i].first >> a[i].second;
    sort(a.begin(), a.end());
    int cnt = 0, curEnd = INT_MIN;
    for (auto& p : a) {
        if (cnt == 0 || p.first > curEnd) { cnt++; curEnd = p.second; }
        else curEnd = max(curEnd, p.second);
    }
    cout << cnt << "\n";
}
