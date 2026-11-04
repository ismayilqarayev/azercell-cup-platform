#include <bits/stdc++.h>
using namespace std;
int bsearch(vector<int>& a, int lo, int hi, int x){
    if (lo > hi) return -1;
    int mid = (lo + hi) / 2;
    if (a[mid] == x) return mid;
    if (a[mid] < x) return bsearch(a, mid + 1, hi, x);
    return bsearch(a, lo, mid - 1, x);
}
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    int x; cin >> x;
    int pos = bsearch(a, 0, n - 1, x);
    cout << (pos == -1 ? -1 : pos + 1) << "\n";
}
