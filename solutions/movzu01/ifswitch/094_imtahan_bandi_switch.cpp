#include <bits/stdc++.h>
using namespace std;

int main(){
    int bal;
    cin >> bal;

    // bal/10 tam bölmə ilə bal-ı 0-10 arası bir "onluğa" salır
    // (məsələn 95/10 = 9, 100/10 = 10). Bu ədəd üzərində switch açırıq.
    switch (bal / 10) {
        case 10: case 9: cout << "A" << "\n"; break;
        case 8: case 7: cout << "B" << "\n"; break;
        case 6: case 5: cout << "C" << "\n"; break;
        default: cout << "D" << "\n";
    }
}
