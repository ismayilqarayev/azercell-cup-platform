#include <bits/stdc++.h>
using namespace std;

int main(){
    long long c;               // Selsi dərəcəsi
    cin >> c;

    // Fizikadan tanış olan düstur: Farenheyt = Selsi * 9/5 + 32.
    // Burada 9 və 5 elə seçilib ki, hasil 5-ə tam bölünsün, ona görə
    // tam ədədlərlə (kəsr olmadan) işləyə bilirik.
    cout << c * 9 / 5 + 32 << "\n";
}
