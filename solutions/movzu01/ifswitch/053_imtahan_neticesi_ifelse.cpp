#include <bits/stdc++.h>
using namespace std;

int main(){
    int bal;
    cin >> bal;

    // Şərtlərin SIRASI vacibdir: əvvəlcə "50-dən az"mı yoxlanılır,
    // sonra "tam 100"mü, qalan hər şey (50-99 arası) "keçdi" olur.
    if (bal < 50)
        cout << "KESILDI" << "\n";
    else if (bal == 100)
        cout << "ELA_NETICE" << "\n";
    else
        cout << "KECDI" << "\n";
}
