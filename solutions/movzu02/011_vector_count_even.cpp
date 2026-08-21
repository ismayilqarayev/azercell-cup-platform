#include <bits/stdc++.h>
using namespace std;

int main(){
    int n; cin >> n;
    vector<long long> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    int cnt = 0;
    for (long long x : a) {
        if (x % 2 == 0) cnt++;
    }

    cout << cnt << "\n";
}
