#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    if (n == 0) { cout << "YOX" << "\n"; return 0; }
    string best;
    for (int i = 0; i < n; i++) {
        string w; cin >> w;
        if (w.size() > best.size()) best = w;
    }
    cout << best << "\n";
}
