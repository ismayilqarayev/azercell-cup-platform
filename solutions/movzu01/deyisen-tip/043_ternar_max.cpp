#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b, c; cin >> a >> b >> c;
    long long mx = (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
    cout << mx << "\n";
}
