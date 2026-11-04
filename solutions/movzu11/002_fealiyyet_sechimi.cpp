#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<pair<int,int>> a(n);
    for (int i = 0; i < n; i++) cin >> a[i].second >> a[i].first; // {bitme, başlama} - end-ə görə sort üçün
    for (int i = 0; i < n; i++) swap(a[i].first, a[i].second);
    sort(a.begin(), a.end(), [](auto& x, auto& y){ return x.second < y.second; });
    int cnt = 0, lastEnd = INT_MIN;
    for (auto& p : a) {
        if (p.first >= lastEnd) { cnt++; lastEnd = p.second; }
    }
    cout << cnt << "\n";
}
