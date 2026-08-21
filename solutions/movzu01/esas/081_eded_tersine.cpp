#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long rev = 0;         // tərsinə çevrilmiş ədəd burada yığılacaq

    // Hər addımda n-in son rəqəmini (n%10) götürüb, onu rev-in SONUNA əlavə
    // edirik (rev = rev*10 + rəqəm — bu, rev-i "sola sürüşdürüb" yeni rəqəmi
    // sağ tərəfdən yapışdırmaq deməkdir), sonra n/=10 ilə həmin rəqəmi n-dən atırıq.
    while (n > 0) {
        rev = rev * 10 + n % 10;
        n /= 10;
    }

    cout << rev << "\n";
}
