#include <bits/stdc++.h>
using namespace std;

int main(){
    int r, c;
    cin >> r >> c;

    long long sum = 0;
    for (int i = 0; i < r; i++) {
        for (int j = 0; j < c; j++) {
            long long x;
            cin >> x;
            sum += x;
        }
    }

    cout << sum << "\n";
}
