#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b; char op; cin >> a >> op >> b;
    long long res = 0;
    if (op == '+') res = a + b;
    else if (op == '-') res = a - b;
    else if (op == '*') res = a * b;
    else if (op == '/') res = a / b;
    cout << res << "\n";
}
