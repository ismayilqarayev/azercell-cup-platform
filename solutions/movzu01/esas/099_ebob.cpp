#include <bits/stdc++.h>
using namespace std;
long long gcdFn(long long a, long long b){ while (b) { long long t = a % b; a = b; b = t; } return a; }
int main(){
    long long a, b; cin >> a >> b;
    cout << gcdFn(a, b) << "\n";
}
