#include <bits/stdc++.h>
using namespace std;
string s;
vector<bool> used;
long long cnt = 0;
void bt(int depth){
    if (depth == (int)s.size()) { cnt++; return; }
    for (int i = 0; i < (int)s.size(); i++) {
        if (used[i]) continue;
        used[i] = true;
        bt(depth + 1);
        used[i] = false;
    }
}
int main(){
    cin >> s;
    used.assign(s.size(), false);
    bt(0);
    cout << cnt << "\n";
}
