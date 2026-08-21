#include <bits/stdc++.h>
using namespace std;

int main(){
    long long w, h;            // en (width) və hündürlük (height)
    cin >> w >> h;

    // Düzbucaqlının sahəsi: en vurulur hündürlüyə.
    cout << w * h << "\n";
    // Perimetri: bütün tərəflərin cəmi, yəni 2*(en+hündürlük).
    cout << 2 * (w + h) << "\n";
}
