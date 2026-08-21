#include <bits/stdc++.h>
using namespace std;

int main(){
    int r, c;
    cin >> r >> c;

    long long a[50][50];
    for (int i = 0; i < r; i++)
        for (int j = 0; j < c; j++)
            cin >> a[i][j];

    int k, m;
    cin >> k >> m;

    long long rowSum = 0;
    for (int j = 0; j < c; j++) rowSum += a[k][j];

    long long colSum = 0;
    for (int i = 0; i < r; i++) colSum += a[i][m];

    cout << rowSum << " " << colSum << "\n";
}
