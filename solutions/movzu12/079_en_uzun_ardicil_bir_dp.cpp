#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    int cur = 0, best = 0;
    for (char c : s) {
        if (c == '1') { cur++; best = max(best, cur); }
        else cur = 0;
    }
    cout << best << "\n";
}
