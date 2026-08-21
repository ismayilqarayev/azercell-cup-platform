#include <bits/stdc++.h>
using namespace std;
int main(){
    char c; cin >> c;
    if (isdigit((unsigned char)c)) cout << "REQEM\n";
    else if (isupper((unsigned char)c)) cout << "BOYUK_HERF\n";
    else if (islower((unsigned char)c)) cout << "KICIK_HERF\n";
    else cout << "DIGER\n";
}
