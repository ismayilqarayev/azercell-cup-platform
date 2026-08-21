#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, k;
    cin >> a >> k;

    // << (sola sürüşdürmə) hər biti k mövqe sola aparır, boşalan yerlərə
    // sıfır yazılır. Bu, a-nı 2^k ədədinə vurmaqla EYNİ nəticəni verir.
    cout << (a << k) << "\n";
}
