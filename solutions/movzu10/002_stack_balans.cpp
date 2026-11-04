#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    stack<char> st;
    bool ok = true;
    for (char c : s) {
        if (c == '(') st.push(c);
        else {
            if (st.empty()) { ok = false; break; }
            st.pop();
        }
    }
    if (!st.empty()) ok = false;
    cout << (ok ? "VAR" : "YOX") << "\n";
}
