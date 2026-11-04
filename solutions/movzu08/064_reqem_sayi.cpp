#include <bits/stdc++.h>
using namespace std;
int digitCount(long long n){
    if (n == 0) return 0;
    return 1 + digitCount(n / 10);
}
int main(){
    long long n; cin >> n;
    cout << (n == 0 ? 1 : digitCount(n)) << "\n";
}
