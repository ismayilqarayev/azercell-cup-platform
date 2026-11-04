#include <bits/stdc++.h>
using namespace std;
int n, k; vector<int> a;
int best;
vector<int> chosen;
void bt(int idx, int cnt){
    if (cnt == k) {
        int mx = *max_element(chosen.begin(), chosen.end());
        int mn = *min_element(chosen.begin(), chosen.end());
        best = min(best, mx - mn);
        return;
    }
    if (idx == n) return;
    if (n - idx < k - cnt) return; // qalan element kifayət etmir
    chosen.push_back(a[idx]);
    bt(idx + 1, cnt + 1);
    chosen.pop_back();
    bt(idx + 1, cnt);
}
int main(){
    cin >> n >> k;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    best = INT_MAX;
    bt(0, 0);
    cout << best << "\n";
}
