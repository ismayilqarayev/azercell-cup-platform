#include <bits/stdc++.h>
using namespace std;

long long vectorCemi(const vector<long long>& v){
    long long s = 0;
    for (long long x : v) s += x;
    return s;
}

int main(){
    int n; cin >> n;
    vector<long long> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    cout << vectorCemi(a) << "\n";
}
