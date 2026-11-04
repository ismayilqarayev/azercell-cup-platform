#include <bits/stdc++.h>
using namespace std;
int n, k;
long long cnt = 0;
void bt(int idx, int remain){
    if (remain == 0) { cnt++; return; }
    if (idx == n) return;
    bt(idx + 1, remain - 1); // seç
    bt(idx + 1, remain);     // keç
}
int main(){
    cin >> n >> k;
    bt(0, k);
    cout << cnt << "\n";
}
