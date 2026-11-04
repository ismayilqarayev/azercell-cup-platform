#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    set<string> s;
    for (int i = 0; i < n; i++) { string w; cin >> w; s.insert(w); }
    cout << s.size() << "\n";
}
