#include <bits/stdc++.h>
using namespace std;
int countEven(vector<int>& a, int idx){
    if (idx == (int)a.size()) return 0;
    int rest = countEven(a, idx + 1);
    return (a[idx] % 2 == 0 ? 1 : 0) + rest;
}
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    cout << countEven(a, 0) << "\n";
}
