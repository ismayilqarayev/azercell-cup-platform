#include <bits/stdc++.h>
using namespace std;
int main(){
    long long a, b; cin >> a >> b;
    double real = (double)a / b;
    double intPart = (double)(a / b);
    cout << fixed << setprecision(2) << real - intPart << "\n";
}
