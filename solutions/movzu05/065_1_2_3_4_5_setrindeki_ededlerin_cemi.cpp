#include <bits/stdc++.h>
using namespace std;
int main(){
    string line;
    getline(cin, line);
    stringstream ss(line);
    long long x, sum = 0;
    while (ss >> x) sum += x;
    cout << sum << "\n";
}
