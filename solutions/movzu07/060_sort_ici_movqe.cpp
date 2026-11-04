#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, idx; cin >> n >> idx;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    sort(a.begin(), a.end());
    cout << a[idx - 1] << "\n";
}
