#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    string s = to_string(n);

    for (int i = 0; i < (int)s.size(); i++) {
        cout << s[i];
        if (i < (int)s.size() - 1) cout << " ";
    }
    cout << "\n";
}
