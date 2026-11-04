#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<pair<int,int>> a(n);
    for (int i = 0; i < n; i++) cin >> a[i].first >> a[i].second;
    sort(a.begin(), a.end(), [](auto& x, auto& y){ return x.second < y.second; });
    int arrows = 0; long long shotAt = LLONG_MIN;
    for (auto& p : a) {
        if (p.first > shotAt) { arrows++; shotAt = p.second; }
    }
    cout << arrows << "\n";
}
