#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a;
    for (int i = 0; i < n; i++) { int x; cin >> x; a.push_back(x); }
    int m; cin >> m;
    for (int i = 0; i < m; i++) { int x; cin >> x; a.push_back(x); }
    sort(a.begin(), a.end());
    for (size_t i = 0; i < a.size(); i++) cout << a[i] << (i + 1 < a.size() ? " " : "\n");
}
