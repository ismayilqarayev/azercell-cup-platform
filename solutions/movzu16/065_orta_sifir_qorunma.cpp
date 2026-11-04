#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    if (n == 0) { cout << "YOX" << "\n"; return 0; }
    long long sum = 0;
    for (int i = 0; i < n; i++) { int x; cin >> x; sum += x; }
    cout << fixed << setprecision(2) << (double)sum / n << "\n";
}
