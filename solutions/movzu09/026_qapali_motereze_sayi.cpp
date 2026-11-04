#include <bits/stdc++.h>
using namespace std;
int n;
long long cnt = 0;
void bt(int open, int close){
    if (open == n && close == n) { cnt++; return; }
    if (open < n) bt(open + 1, close);
    if (close < open) bt(open, close + 1);
}
int main(){
    cin >> n;
    bt(0, 0);
    cout << cnt << "\n";
}
