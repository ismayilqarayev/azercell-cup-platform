#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    map<int,int> freq;
    for (int i = 0; i < n; i++) { int x; cin >> x; freq[x]++; }
    int best = 0;
    for (auto& p : freq) best = max(best, p.second);
    cout << best << "\n";
}
