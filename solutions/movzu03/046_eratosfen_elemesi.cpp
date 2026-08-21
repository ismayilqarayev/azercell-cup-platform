#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    bool eleme[100005];
    for (int i = 0; i <= n; i++) eleme[i] = true;
    eleme[0] = false;
    if (n >= 1) eleme[1] = false;

    for (int i = 2; (long long)i * i <= n; i++) {
        if (eleme[i]) {
            for (int j = i * i; j <= n; j += i) {
                eleme[j] = false;
            }
        }
    }

    bool first = true;
    for (int i = 2; i <= n; i++) {
        if (eleme[i]) {
            if (!first) cout << " ";
            cout << i;
            first = false;
        }
    }
    cout << "\n";
}
