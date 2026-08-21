#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // a-nın b-ci qüvvəti (a^b), a-nı özü-özünə b dəfə vurmaqla tapılır.
    // pow() funksiyasından istifadə etmirik — sadəcə dövrdə b dəfə vururuq.
    long long res = 1;
    for (long long i = 0; i < b; i++) {
        res *= a;
    }

    cout << res << "\n";
}
