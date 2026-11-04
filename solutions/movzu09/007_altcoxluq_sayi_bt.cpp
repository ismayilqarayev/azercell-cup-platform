#include <bits/stdc++.h>
using namespace std;
int n;
long long cnt = 0;
void bt(int idx){
    if (idx == n) { cnt++; return; }
    bt(idx + 1);      // daxil etmə
    bt(idx + 1);      // daxil et
}
int main(){
    cin >> n;
    bt(0);
    cout << cnt << "\n";
}
