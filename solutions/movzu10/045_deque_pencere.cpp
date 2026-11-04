#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, k; cin >> n >> k;
    deque<int> dq;
    for (int i = 0; i < n; i++) { int x; cin >> x; dq.push_back(x); }
    for (int i = 0; i < k; i++) { dq.pop_front(); dq.pop_back(); }
    long long sum = 0;
    for (int x : dq) sum += x;
    cout << sum << "\n";
}
