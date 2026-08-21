#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    vector<long long> a;
    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        a.push_back(x);
    }

    long long sum = 0;
    for (long long x : a) sum += x;

    cout << sum << "\n";
}
