#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long a[50][50];
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            cin >> a[i][j];

    long long sum = 0;
    for (int i = 0; i < n; i++) {
        sum += a[i][i];
    }

    cout << sum << "\n";
}
