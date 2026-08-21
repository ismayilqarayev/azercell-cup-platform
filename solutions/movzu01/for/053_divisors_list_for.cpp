#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    bool first = true;
    for (int i = 1; i <= n; i++) {
        if (n % i == 0) {
            if (!first) cout << " ";
            cout << i;
            first = false;
        }
    }
    cout << "\n";
}
