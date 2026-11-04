#include <bits/stdc++.h>
using namespace std;
int main(){
    string a, b; cin >> a >> b;
    map<char,int> fa, fb;
    for (char c : a) fa[c]++;
    for (char c : b) fb[c]++;
    cout << (fa == fb ? "BELI" : "XEYR") << "\n";
}
