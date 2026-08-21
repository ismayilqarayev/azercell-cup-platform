#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long f = 1;
    for (long long i = 2; i <= n; i++) {
        f *= i;
    }

    cout << f << "\n";
}
