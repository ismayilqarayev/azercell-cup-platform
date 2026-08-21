#include <bits/stdc++.h>
using namespace std;
int main(){
    int a, b, c; cin >> a >> b >> c;
    if (!(a + b > c && a + c > b && b + c > a)) { cout << "UCBUCAQ_DEYIL\n"; return 0; }
    if (a == b && b == c) cout << "BERABERTEREFLI\n";
    else if (a == b || b == c || a == c) cout << "BERABERYANLI\n";
    else cout << "MUXTELIF\n";
}
