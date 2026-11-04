#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    vector<int> ans(n, -1);
    stack<int> st; // indekslər
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] <= a[i]) st.pop();
        if (!st.empty()) ans[i] = a[st.top()];
        st.push(i);
    }
    long long sum = 0;
    for (int x : ans) sum += x;
    cout << sum << "\n";
}
