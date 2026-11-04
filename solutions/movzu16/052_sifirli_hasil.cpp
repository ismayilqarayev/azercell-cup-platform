#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    long long prod = 1;
    for (int i = 0; i < n; i++) { int x; cin >> x; prod *= x; }
    cout << prod << "\n";
}
