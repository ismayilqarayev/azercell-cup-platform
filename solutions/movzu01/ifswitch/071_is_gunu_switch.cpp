#include <bits/stdc++.h>
using namespace std;

int main(){
    int k;
    cin >> k;

    // case 1-dən case 5-ə qədər break QOYMURUQ — hamısı eyni "IS_GUNU"
    // nəticəsinə "düşür" (fallthrough). Eyni fikir 6 və 7 üçün də tətbiq olunur.
    switch (k) {
        case 1: case 2: case 3: case 4: case 5:
            cout << "IS_GUNU" << "\n";
            break;
        case 6: case 7:
            cout << "HEFTE_SONU" << "\n";
            break;
    }
}
