#include <bits/stdc++.h>
using namespace std;
int n;
vector<int> col; // col[row] = yerləşdirilmiş sütun
long long cnt = 0;
bool ok(int row, int c){
    for (int r = 0; r < row; r++) {
        if (col[r] == c) return false;
        if (abs(col[r] - c) == abs(r - row)) return false;
    }
    return true;
}
void bt(int row){
    if (row == n) { cnt++; return; }
    for (int c = 0; c < n; c++) {
        if (ok(row, c)) {
            col[row] = c;
            bt(row + 1);
        }
    }
}
int main(){
    cin >> n;
    col.assign(n, -1);
    bt(0);
    cout << cnt << "\n";
}
