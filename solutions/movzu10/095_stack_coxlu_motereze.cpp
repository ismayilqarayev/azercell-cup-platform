#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    stack<char> st;
    bool ok = true;
    for (char c : s) {
        if (c == '(' || c == '[' || c == '{') st.push(c);
        else {
            if (st.empty()) { ok = false; break; }
            char top = st.top(); st.pop();
            if ((c == ')' && top != '(') || (c == ']' && top != '[') || (c == '}' && top != '{')) { ok = false; break; }
        }
    }
    if (!st.empty()) ok = false;
    cout << (ok ? "VAR" : "YOX") << "\n";
}
