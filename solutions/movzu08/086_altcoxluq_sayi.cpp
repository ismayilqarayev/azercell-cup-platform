#include <bits/stdc++.h>
using namespace std;
long long subsets(int n){
    if (n == 0) return 1;
    return 2 * subsets(n - 1);
}
int main(){
    int n; cin >> n;
    cout << subsets(n) << "\n";
}
