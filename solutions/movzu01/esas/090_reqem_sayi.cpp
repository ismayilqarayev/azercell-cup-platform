#include <bits/stdc++.h>
using namespace std;
int main(){
    long long n; cin >> n;
    if (n == 0) { cout << 1 << "\n"; return 0; }
    int cnt = 0;
    while (n > 0) { cnt++; n /= 10; }
    cout << cnt << "\n";
}
