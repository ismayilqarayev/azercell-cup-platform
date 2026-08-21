#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    string s = to_string(n);

    bool artandir = true;
    for (int i = 0; i + 1 < (int)s.size(); i++) {
        if (s[i] > s[i + 1]) {
            artandir = false;
            break;
        }
    }

    cout << (artandir ? "BELE" : "XEYR") << "\n";
}
