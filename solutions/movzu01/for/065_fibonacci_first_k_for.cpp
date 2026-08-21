#include <bits/stdc++.h>
using namespace std;
int main(){
    int k; cin >> k;
    long long a = 0, b = 1;
    for (int i = 0; i < k; i++) {
        cout << a;
        if (i < k - 1) cout << " ";
        long long t = a + b; a = b; b = t;
    }
    cout << "\n";
}
