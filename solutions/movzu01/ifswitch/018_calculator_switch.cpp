#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    char op;
    cin >> a >> op >> b;

    long long res = 0;
    // switch(op) — op simvoluna görə uyğun case-ə keçib, həmin əməliyyatı yerinə yetiririk.
    switch (op) {
        case '+': res = a + b; break;
        case '-': res = a - b; break;
        case '*': res = a * b; break;
        case '/': res = a / b; break;
    }

    cout << res << "\n";
}
