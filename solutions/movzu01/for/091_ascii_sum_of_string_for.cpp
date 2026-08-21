#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    long long sum = 0;
    for (size_t i = 0; i < s.size(); i++) sum += (int)s[i];
    cout << sum << "\n";
}
