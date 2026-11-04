#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, k; cin >> n >> k;
    priority_queue<int, vector<int>, greater<int>> pq;
    for (int i = 0; i < n; i++) { int x; cin >> x; pq.push(x); }
    for (int i = 0; i < k; i++) {
        int x = pq.top(); pq.pop();
        pq.push(-x);
    }
    long long sum = 0;
    while (!pq.empty()) { sum += pq.top(); pq.pop(); }
    cout << sum << "\n";
}
