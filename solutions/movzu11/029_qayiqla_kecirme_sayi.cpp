#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, l; cin >> n >> l;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    sort(a.begin(), a.end());
    int lo = 0, hi = n - 1, boats = 0;
    while (lo <= hi) {
        if (lo < hi && a[lo] + a[hi] <= l) lo++;
        hi--;
        boats++;
    }
    cout << boats << "\n";
}
