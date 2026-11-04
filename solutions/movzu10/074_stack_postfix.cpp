#include <bits/stdc++.h>
using namespace std;
int main(){
    string tok;
    stack<long long> st;
    while (cin >> tok) {
        if (tok == "+" || tok == "-" || tok == "*" || tok == "/") {
            long long b = st.top(); st.pop();
            long long a = st.top(); st.pop();
            long long r;
            if (tok == "+") r = a + b;
            else if (tok == "-") r = a - b;
            else if (tok == "*") r = a * b;
            else r = a / b;
            st.push(r);
        } else {
            st.push(stoll(tok));
        }
    }
    cout << st.top() << "\n";
}
