#include <bits/stdc++.h>
using namespace std;
int main(){
    int p1, p2; cin >> p1 >> p2;
    if (p1 == p2) { cout << "HEC-HECE\n"; return 0; }
    if ((p1 == 1 && p2 == 3) || (p1 == 3 && p2 == 2) || (p1 == 2 && p2 == 1))
        cout << "1-CI OYUNCU\n";
    else
        cout << "2-CI OYUNCU\n";
}
