#include <bits/stdc++.h>
using namespace std;
int main(){
    long long x, y; cin >> x >> y;
    if (x == 0 || y == 0) { cout << "OX_UZERINDE\n"; return 0; }
    if (x > 0 && y > 0) cout << "I\n";
    else if (x < 0 && y > 0) cout << "II\n";
    else if (x < 0 && y < 0) cout << "III\n";
    else cout << "IV\n";
}
