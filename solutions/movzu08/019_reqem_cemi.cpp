#include <bits/stdc++.h>
using namespace std;
long long digitSum(long long n){
    if (n == 0) return 0;
    return (n % 10) + digitSum(n / 10);
}
int main(){
    long long n; cin >> n;
    cout << digitSum(n) << "\n";
}
