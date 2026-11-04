#include <bits/stdc++.h>
using namespace std;
string s;
long long cnt = 0;
bool isPal(int l, int r){
    while (l < r) { if (s[l] != s[r]) return false; l++; r--; }
    return true;
}
void bt(int start){
    if (start == (int)s.size()) { cnt++; return; }
    for (int end = start; end < (int)s.size(); end++) {
        if (isPal(start, end)) bt(end + 1);
    }
}
int main(){
    cin >> s;
    bt(0);
    cout << cnt << "\n";
}
