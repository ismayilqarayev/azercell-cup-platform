#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, m;
    cin >> a >> b >> m;

    long long res = ((a - b) % m + m) % m;

    cout << res << "\n";
}
