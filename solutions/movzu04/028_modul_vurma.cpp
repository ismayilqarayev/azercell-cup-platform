#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, m;
    cin >> a >> b >> m;

    cout << (a % m) * (b % m) % m << "\n";
}
