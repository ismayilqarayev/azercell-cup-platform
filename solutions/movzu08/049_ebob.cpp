#include <bits/stdc++.h>
using namespace std;
long long gcdRec(long long a, long long b){
    if (b == 0) return a;
    return gcdRec(b, a % b);
}
int main(){
    long long a, b; cin >> a >> b;
    cout << gcdRec(a, b) << "\n";
}
