#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b; cin >> a >> b;
    long long cnt = 0;
    for (long long i = a; i <= b; i++) if (i % 2 == 0) cnt++;
    cout << cnt << "\n";
}
