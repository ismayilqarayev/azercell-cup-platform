#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b; char op; cin >> a >> op >> b;
    long long res = 0;
    switch (op) {
        case '+': res = a + b; break;
        case '-': res = a - b; break;
        case '*': res = a * b; break;
        case '/': res = a / b; break;
    }
    cout << res << "\n";
}
