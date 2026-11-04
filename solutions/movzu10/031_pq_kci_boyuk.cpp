#include <bits/stdc++.h>
using namespace std;
int main(){
    int n, k; cin >> n >> k;
    priority_queue<int, vector<int>, greater<int>> pq;
    for (int i = 0; i < n; i++) {
        int x; cin >> x;
        pq.push(x);
        if ((int)pq.size() > k) pq.pop();
    }
    cout << pq.top() << "\n";
}
