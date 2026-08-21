#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // +=, -=, *= — "mürəkkəb mənimsətmə" operatorlarıdır, qısa yazılışdır:
    // a += b   eyni şeydir ki   a = a + b
    a += b;    // a-nı b qədər artırırıq
    a -= 3;    // sonra a-dan 3 çıxırıq
    a *= 2;    // sonra a-nı 2-yə vururuq
    // Hər addım əvvəlki addımın NƏTİCƏSİ üzərindən davam edir.

    cout << a << "\n";
}
