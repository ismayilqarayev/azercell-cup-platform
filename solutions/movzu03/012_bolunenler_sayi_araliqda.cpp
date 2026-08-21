#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, k;
    cin >> a >> b >> k;

    long long cnt = 0;
    for (long long i = a; i <= b; i++) {
        if (i % k == 0) cnt++;
    }

    cout << cnt << "\n";
}
