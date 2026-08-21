#include <bits/stdc++.h>
using namespace std;
int main(){
    long long n; cin >> n;
    long long f = 1, i = 1;
    while (i <= n) { f *= i; i++; }
    cout << f << "\n";
}
