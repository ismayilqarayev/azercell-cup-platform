#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long mx;
    cin >> mx;             // ilk ədədi birbaşa "indiyədək ən böyük" kimi götürürük

    // qalan n-1 ədədi oxuyuruq, hər birini mx ilə müqayisə edirik —
    // əgər yeni ədəd mx-dən böyükdürsə, mx-i yeniləyirik.
    for (int i = 1; i < n; i++) {
        long long x;
        cin >> x;
        if (x > mx) mx = x;
    }

    cout << mx << "\n";
}
