#include <bits/stdc++.h>
using namespace std;
int main(){
    char c; int k; cin >> c >> k;
    int idx = (c - 'a' + k) % 26;
    cout << (char)('a' + idx) << "\n";
}
