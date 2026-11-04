#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> starts(n), ends(n);
    for (int i = 0; i < n; i++) cin >> starts[i] >> ends[i];
    sort(starts.begin(), starts.end());
    sort(ends.begin(), ends.end());
    int i = 0, j = 0, cur = 0, best = 0;
    while (i < n) {
        if (starts[i] < ends[j]) { cur++; i++; best = max(best, cur); }
        else { cur--; j++; }
    }
    cout << best << "\n";
}
