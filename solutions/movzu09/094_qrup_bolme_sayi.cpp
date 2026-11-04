#include <bits/stdc++.h>
using namespace std;
int n;
long long cnt = 0;
void bt(int idx, int sizeA, int sizeB){
    if (idx == n) { if (sizeA > 0 && sizeB > 0) cnt++; return; }
    bt(idx + 1, sizeA + 1, sizeB);
    bt(idx + 1, sizeA, sizeB + 1);
}
int main(){
    cin >> n;
    bt(0, 0, 0);
    cout << (cnt / 2) << "\n";
}
