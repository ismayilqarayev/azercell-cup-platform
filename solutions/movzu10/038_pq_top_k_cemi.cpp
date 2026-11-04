#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, k; cin >> n >> k;
    priority_queue<long long> pq;
    for (int i = 0; i < n; i++) { long long x; cin >> x; pq.push(x); }
    long long sum = 0;
    for (int i = 0; i < k; i++) { sum += pq.top(); pq.pop(); }
    cout << sum << "\n";
}
