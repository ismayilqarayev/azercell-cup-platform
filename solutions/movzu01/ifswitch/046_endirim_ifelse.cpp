#include <bits/stdc++.h>
using namespace std;
int main(){
    long long m; cin >> m;
    long long faiz;
    if (m < 100) faiz = 0;
    else if (m < 500) faiz = 5;
    else if (m < 1000) faiz = 10;
    else faiz = 15;
    cout << m - m * faiz / 100 << "\n";
}
