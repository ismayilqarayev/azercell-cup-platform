#include <bits/stdc++.h>
using namespace std;
int countChar(const string& s, char c){
    if (s.empty()) return 0;
    int first = (s[0] == c) ? 1 : 0;
    return first + countChar(s.substr(1), c);
}
int main(){
    string s; cin >> s;
    char c; cin >> c;
    cout << countChar(s, c) << "\n";
}
