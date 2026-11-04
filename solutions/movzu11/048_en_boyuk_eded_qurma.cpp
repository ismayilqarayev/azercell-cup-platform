#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> d(n);
    for (int i = 0; i < n; i++) cin >> d[i];
    sort(d.begin(), d.end(), greater<int>());
    string res;
    for (int x : d) res += char('0' + x);
    cout << res << "\n";
}
