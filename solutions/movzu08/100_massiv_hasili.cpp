#include <bits/stdc++.h>
using namespace std;
long long prodArr(vector<int>& a, int idx){
    if (idx == (int)a.size()) return 1;
    return a[idx] * prodArr(a, idx + 1);
}
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    cout << prodArr(a, 0) << "\n";
}
