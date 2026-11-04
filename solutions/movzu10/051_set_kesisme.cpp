#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    set<int> a;
    for (int i = 0; i < n; i++) { int x; cin >> x; a.insert(x); }
    int m; cin >> m;
    set<int> common;
    for (int i = 0; i < m; i++) {
        int x; cin >> x;
        if (a.count(x)) common.insert(x);
    }
    cout << common.size() << "\n";
}
