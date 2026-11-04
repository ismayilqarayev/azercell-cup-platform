#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, k; cin >> n >> k;
    priority_queue<int> pq;
    for (int i = 0; i < n; i++) {
        int x; cin >> x;
        pq.push(x);
        if ((int)pq.size() > k) pq.pop();
    }
    long long sum = 0;
    while (!pq.empty()) { sum += pq.top(); pq.pop(); }
    cout << sum << "\n";
}
