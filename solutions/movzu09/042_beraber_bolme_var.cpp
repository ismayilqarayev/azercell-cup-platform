#include <bits/stdc++.h>
using namespace std;
int n; vector<int> a;
bool bt(int idx, int remain){
    if (remain == 0) return true;
    if (idx == n || remain < 0) return false;
    if (bt(idx + 1, remain - a[idx])) return true;
    return bt(idx + 1, remain);
}
int main(){
    cin >> n;
    a.resize(n);
    int total = 0;
    for (int i = 0; i < n; i++) { cin >> a[i]; total += a[i]; }
    if (total % 2 != 0) { cout << "YOX" << "\n"; return 0; }
    cout << (bt(0, total / 2) ? "VAR" : "YOX") << "\n";
}
