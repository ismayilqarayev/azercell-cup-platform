#include <bits/stdc++.h>
using namespace std;
long long sum1n(int n){
    if (n == 0) return 0;
    return n + sum1n(n - 1);
}
int main(){
    int n; cin >> n;
    cout << sum1n(n) << "\n";
}
