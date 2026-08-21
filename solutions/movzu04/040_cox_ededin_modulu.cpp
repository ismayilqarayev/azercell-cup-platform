#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    long long m;
    cin >> n >> m;

    long long sum = 0;
    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        sum = (sum + x) % m;
    }

    cout << sum << "\n";
}
