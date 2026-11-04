#include <bits/stdc++.h>
using namespace std;
bool search(vector<int>& a, int idx, int x){
    if (idx == (int)a.size()) return false;
    if (a[idx] == x) return true;
    return search(a, idx + 1, x);
}
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    int x; cin >> x;
    cout << (search(a, 0, x) ? "VAR" : "YOX") << "\n";
}
