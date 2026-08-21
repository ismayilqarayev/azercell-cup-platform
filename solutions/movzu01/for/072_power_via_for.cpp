#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b; cin >> a >> b;
    long long res = 1;
    for (long long i = 0; i < b; i++) res *= a;
    cout << res << "\n";
}
