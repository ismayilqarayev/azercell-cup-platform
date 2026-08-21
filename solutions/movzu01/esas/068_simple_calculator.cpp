#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    char op;                   // əməliyyat işarəsi: '+', '-', '*' və ya '/'
    cin >> a >> op >> b;

    long long res = 0;

    // op dəyişəninin hansı işarə olduğunu ardıcıl yoxlayırıq
    // və uyğun riyazi əməliyyatı yerinə yetiririk.
    if (op == '+') res = a + b;
    else if (op == '-') res = a - b;
    else if (op == '*') res = a * b;
    else if (op == '/') res = a / b;

    cout << res << "\n";
}
