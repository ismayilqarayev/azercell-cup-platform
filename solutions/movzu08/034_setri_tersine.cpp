#include <bits/stdc++.h>
using namespace std;
string rev(const string& s){
    if (s.empty()) return s;
    return rev(s.substr(1)) + s[0];
}
int main(){
    string s; cin >> s;
    cout << rev(s) << "\n";
}
