#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    map<int,int> freq;
    for (int i = 0; i < n; i++) { int x; cin >> x; freq[x]++; }
    int bestVal = 0, bestCnt = -1;
    for (auto& p : freq) {
        if (p.second > bestCnt) { bestCnt = p.second; bestVal = p.first; }
    }
    cout << bestVal << "\n";
}
