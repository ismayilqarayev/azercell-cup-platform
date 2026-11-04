#include <bits/stdc++.h>
using namespace std;
long long power(long long a, int n){
    if (n == 0) return 1;
    return a * power(a, n - 1);
}
int main(){
    long long a; int n; cin >> a >> n;
    cout << power(a, n) << "\n";
}
