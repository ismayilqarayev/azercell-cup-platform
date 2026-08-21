#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b; cin >> a >> b;
    a ^= b;
    b ^= a;
    a ^= b;
    cout << a << " " << b << "\n";
}
