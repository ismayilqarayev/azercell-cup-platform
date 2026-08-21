#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    bool isPowerOf2 = (n > 0) && ((n & (n - 1)) == 0);

    cout << (isPowerOf2 ? "BELE" : "XEYR") << "\n";
}
