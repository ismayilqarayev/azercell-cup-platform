#include <bits/stdc++.h>
using namespace std;
int main(){
    long long n; cin >> n;
    long long rev = 0;
    while (n > 0) { rev = rev * 10 + n % 10; n /= 10; }
    cout << rev << "\n";
}
