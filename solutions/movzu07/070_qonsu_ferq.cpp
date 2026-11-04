#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    sort(a.begin(), a.end());
    int best = 0;
    for (int i = 1; i < n; i++) best = max(best, a[i] - a[i - 1]);
    cout << best << "\n";
}
