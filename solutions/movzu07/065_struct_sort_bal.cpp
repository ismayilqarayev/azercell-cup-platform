#include <bits/stdc++.h>
using namespace std;
struct St { string ad; int bal; };
int main(){
    int n; cin >> n;
    vector<St> s(n);
    for (int i = 0; i < n; i++) cin >> s[i].ad >> s[i].bal;
    stable_sort(s.begin(), s.end(), [](const St& x, const St& y){ return x.bal > y.bal; });
    for (int i = 0; i < n; i++) cout << s[i].ad << " " << s[i].bal << "\n";
}
