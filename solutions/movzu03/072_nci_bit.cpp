#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n, i;
    cin >> n >> i;

    long long bit = (n >> i) & 1;

    cout << bit << "\n";
}
