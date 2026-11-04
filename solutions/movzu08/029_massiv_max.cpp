#include <bits/stdc++.h>
using namespace std;
int maxArr(vector<int>& a, int idx){
    if (idx == (int)a.size() - 1) return a[idx];
    int restMax = maxArr(a, idx + 1);
    return max(a[idx], restMax);
}
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    cout << maxArr(a, 0) << "\n";
}
