#include <bits/stdc++.h>
using namespace std;
int main(){
    int ay; cin >> ay;
    int gun;
    switch (ay) {
        case 2: gun = 28; break;
        case 4: case 6: case 9: case 11: gun = 30; break;
        default: gun = 31;
    }
    cout << gun << "\n";
}
