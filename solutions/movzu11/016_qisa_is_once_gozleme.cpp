#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    sort(a.begin(), a.end());
    long long total = 0, finish = 0;
    for (int x : a) { finish += x; total += finish; }
    cout << total << "\n";
}
