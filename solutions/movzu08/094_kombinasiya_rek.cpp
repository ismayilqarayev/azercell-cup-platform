#include <bits/stdc++.h>
using namespace std;
long long C(int n, int k){
    if (k == 0 || k == n) return 1;
    return C(n - 1, k - 1) + C(n - 1, k);
}
int main(){
    int n, k; cin >> n >> k;
    cout << C(n, k) << "\n";
}
