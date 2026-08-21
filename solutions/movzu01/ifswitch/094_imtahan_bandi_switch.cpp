#include <bits/stdc++.h>
using namespace std;
int main(){
    int bal; cin >> bal;
    switch (bal / 10) {
        case 10: case 9: cout << "A\n"; break;
        case 8: case 7: cout << "B\n"; break;
        case 6: case 5: cout << "C\n"; break;
        default: cout << "D\n";
    }
}
