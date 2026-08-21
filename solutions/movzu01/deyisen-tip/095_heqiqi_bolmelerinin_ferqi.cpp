#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // Əvvəlcə dəqiq (kəsrli) bölməni hesablayırıq.
    double real = (double)a / b;
    // Sonra tam ədəd bölməsini (kəsr hissə atılmış) tapıb, onu da double-a çeviririk.
    double intPart = (double)(a / b);

    // İkisinin fərqi bizə "atılan kəsr hissəni" göstərir.
    cout << fixed << setprecision(2) << real - intPart << "\n";
}
