#include <bits/stdc++.h>
using namespace std;
int main(){
    int ay; cin >> ay;
    if (ay == 12 || ay == 1 || ay == 2) cout << "QIS\n";
    else if (ay >= 3 && ay <= 5) cout << "YAZ\n";
    else if (ay >= 6 && ay <= 8) cout << "YAY\n";
    else cout << "PAYIZ\n";
}
