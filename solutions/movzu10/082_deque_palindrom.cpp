#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    deque<char> dq(s.begin(), s.end());
    bool ok = true;
    while (dq.size() > 1) {
        if (dq.front() != dq.back()) { ok = false; break; }
        dq.pop_front();
        dq.pop_back();
    }
    cout << (ok ? "BELI" : "XEYR") << "\n";
}
