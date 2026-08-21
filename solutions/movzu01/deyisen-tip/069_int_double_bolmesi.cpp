#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // İki tam ədədi (long long) bölərkən nəticə də tam ədəddir —
    // kəsr hissə atılır (məs. 7/2 = 3, 3.5 yox).
    cout << a / b << "\n";

    // Dəqiq (kəsrli) nəticə almaq üçün operandlardan birini (double)-a
    // çeviririk — onda C++ artıq həqiqi bölmə aparır.
    cout << fixed << setprecision(2) << (double)a / b << "\n";
}
