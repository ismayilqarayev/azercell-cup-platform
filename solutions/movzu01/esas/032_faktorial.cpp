#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    // n! (n faktorial) = 1 * 2 * 3 * ... * n. Nəticəni f dəyişənində yığırıq,
    // əvvəlcə f=1 götürürük (vurmanın "boş" (neytral) dəyəri 1-dir).
    long long f = 1;
    long long i = 1;
    // i, 1-dən n-ə qədər hər addımda 1 artır, hər dəfə f-i i-yə vururuq.
    while (i <= n) {
        f *= i;   // f = f * i ilə eynidir
        i++;      // növbəti ədədə keçirik
    }

    // f uzun ədəd (long long) tipindədir ki, böyük n üçün ədəd "daşmasın".
    cout << f << "\n";
}
