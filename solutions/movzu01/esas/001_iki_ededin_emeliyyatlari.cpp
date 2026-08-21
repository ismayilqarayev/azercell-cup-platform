#include <bits/stdc++.h>   // Lazımi bütün kitabxanaları (cin, cout və s.) bir dəfəyə qoşur
using namespace std;         // "std::" hissəsini hər dəfə yazmamaq üçün

int main(){
    long long a, b;          // İki tam ədəd saxlayacaq dəyişənlər
    cin >> a >> b;            // Klaviaturadan (test faylından) a və b oxunur

    // Beş əməliyyatı sırayla hesablayıb, hər birini AYRI sətirdə çap edirik
    cout << a + b << "\n";   // cəm (toplama)
    cout << a - b << "\n";   // fərq (çıxma)
    cout << a * b << "\n";   // hasil (vurma)
    cout << a / b << "\n";   // tam bölmə — kəsr hissə atılır (məs. 17/5 = 3, 3.4 yox)
    cout << a % b << "\n";   // qalıq — bölmədən sonra qalan hissə (məs. 17%5 = 2)
}
