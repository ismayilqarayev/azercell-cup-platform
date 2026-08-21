#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    // 1-dən N-ə qədər bütün ədədlərin cəmini tapmaq üçün 1+2+...+n şəklində
    // dövrlə də saya bilərdik, amma riyaziyyatdan tanış olan Qauss düsturu
    // birbaşa nəticəni verir: cəm = n*(n+1)/2. Bu, çox böyük N üçün də sürətlidir.
    cout << n * (n + 1) / 2 << "\n";
}
