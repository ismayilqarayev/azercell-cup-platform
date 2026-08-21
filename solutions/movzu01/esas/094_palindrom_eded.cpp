#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long orig = n;        // orijinal (dəyişməmiş) ədədi yadda saxlayırıq
    long long rev = 0;

    // Əvvəlki tapşırıqdakı kimi ədədi tərsinə çeviririk
    while (n > 0) {
        rev = rev * 10 + n % 10;
        n /= 10;
    }

    // Əgər tərs dəyər orijinal ədədlə eynidirsə, deməli ədəd palindromdur
    // (irəli və geri oxunuşu eynidir, məs. 12321).
    cout << (rev == orig ? "BELE" : "XEYR") << "\n";
}
