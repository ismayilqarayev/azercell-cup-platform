#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long res = 1;
    for (long long i = 0; i < n; i++) {
        res *= 2;
    }

    cout << res << "\n";
}
