#include <bits/stdc++.h>
using namespace std;

int main(){
    string s;
    cin >> s;

    long long n = 0;
    for (char c : s) {
        n = n * 8 + (c - '0');
    }

    cout << n << "\n";
}
