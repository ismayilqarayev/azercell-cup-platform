#include <bits/stdc++.h>
using namespace std;

int main(){
    int r, c;
    cin >> r >> c;

    long long a[50][50];
    for (int i = 0; i < r; i++)
        for (int j = 0; j < c; j++)
            cin >> a[i][j];

    for (int j = 0; j < c; j++) {
        for (int i = 0; i < r; i++) {
            cout << a[i][j];
            if (i < r - 1) cout << " ";
        }
        cout << "\n";
    }
}
