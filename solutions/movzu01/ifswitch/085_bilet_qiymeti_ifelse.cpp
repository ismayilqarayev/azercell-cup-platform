#include <bits/stdc++.h>
using namespace std;
int main(){
    int yas; cin >> yas;
    int baza = 20;
    if (yas < 6) cout << 0 << "\n";
    else if (yas <= 17) cout << baza * 50 / 100 << "\n";
    else if (yas <= 64) cout << baza << "\n";
    else cout << baza * 70 / 100 << "\n";
}
