#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, c;
    cin >> a >> b >> c;

    // Ternar operator (şərt) ? (doğrudursa) : (yanlışdırsa) — if/else-in
    // qısa yazılış formasıdır. Əvvəlcə a ilə b müqayisə olunur, sonra
    // qalib c ilə müqayisə olunur — nəticədə üçünün ən böyüyü tapılır.
    long long mx = (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);

    cout << mx << "\n";
}
