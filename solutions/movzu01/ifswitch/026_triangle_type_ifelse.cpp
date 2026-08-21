#include <bits/stdc++.h>
using namespace std;

int main(){
    int a, b, c;   // üç tərəfin uzunluqları
    cin >> a >> b >> c;

    // Üçbucaq qaydası: istənilən iki tərəfin cəmi üçüncüdən BÖYÜK olmalıdır.
    // Bu şərt ödənmirsə, belə üçbucaq mövcud DEYİL.
    if (!(a + b > c && a + c > b && b + c > a)) {
        cout << "UCBUCAQ_DEYIL" << "\n";
        return 0;   // proqramı burada bitiririk, aşağıya keçmirik
    }

    // Bütün tərəflər bərabərdirsə — bərabərtərəfli.
    if (a == b && b == c)
        cout << "BERABERTEREFLI" << "\n";
    // İstənilən İKİ tərəf bərabərdirsə — bərabəryanlı.
    else if (a == b || b == c || a == c)
        cout << "BERABERYANLI" << "\n";
    // Heç biri bərabər deyilsə — müxtəlif tərəfli.
    else
        cout << "MUXTELIF" << "\n";
}
