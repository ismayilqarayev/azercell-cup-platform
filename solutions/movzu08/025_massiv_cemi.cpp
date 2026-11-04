#include <bits/stdc++.h>
using namespace std;
long long sumArr(vector<int>& a, int idx){
    if (idx == (int)a.size()) return 0;
    return a[idx] + sumArr(a, idx + 1);
}
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    cout << sumArr(a, 0) << "\n";
}
