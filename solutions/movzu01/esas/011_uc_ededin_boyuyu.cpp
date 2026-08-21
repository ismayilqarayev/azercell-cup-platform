#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, c;
    cin >> a >> b >> c;

    // Əvvəlcə "ən böyük indiyə qədər" kimi mx-ə birinci ədədi yazırıq.
    long long mx = a;

    // Sonra qalan iki ədədi növbə ilə mx ilə müqayisə edirik —
    // əgər onlardan biri mx-dən böyükdürsə, mx-i o dəyərlə əvəz edirik.
    if (b > mx) mx = b;
    if (c > mx) mx = c;

    // Bütün müqayisələr bitəndə mx ən böyük ədəddir.
    cout << mx << "\n";
}
