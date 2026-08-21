#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, k;
    cin >> a >> k;

    // >> (sağa sürüşdürmə) hər biti k mövqe sağa aparır. Mənfi olmayan
    // ədədlər üçün bu, a-nı 2^k-ya TAM bölməklə eyni nəticəni verir.
    cout << (a >> k) << "\n";
}
