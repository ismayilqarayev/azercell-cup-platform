#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, m; cin >> a >> m;
    long long r = ((a % m) + m) % m;
    cout << r << "\n";
}
