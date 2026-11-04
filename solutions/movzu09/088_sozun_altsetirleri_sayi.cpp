#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    set<string> subs;
    for (int i = 0; i < (int)s.size(); i++)
        for (int j = i; j < (int)s.size(); j++)
            subs.insert(s.substr(i, j - i + 1));
    cout << subs.size() << "\n";
}
