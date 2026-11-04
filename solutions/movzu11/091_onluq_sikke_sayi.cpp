#include <bits/stdc++.h>
using namespace std;
int main(){
    int a; cin >> a;
    int coins[] = {100, 10, 1};
    int cnt = 0;
    for (int c : coins) { cnt += a / c; a %= c; }
    cout << cnt << "\n";
}
