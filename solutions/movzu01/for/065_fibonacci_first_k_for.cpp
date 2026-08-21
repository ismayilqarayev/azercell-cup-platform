#include <bits/stdc++.h>
using namespace std;

int main(){
    int k;
    cin >> k;

    // Fibonaççi ardıcıllığında hər hədd özündən əvvəlki İKİ həddin cəmidir:
    // 0, 1, 1, 2, 3, 5, 8, 13, ... a və b həmişə "indiki" iki qonşu həddi saxlayır.
    long long a = 0, b = 1;

    for (int i = 0; i < k; i++) {
        cout << a;
        if (i < k - 1) cout << " ";

        // növbəti həddi hesablayıb a və b-ni "irəli sürüşdürürük"
        long long t = a + b;   // yeni hədd
        a = b;                 // köhnə b, yeni a olur
        b = t;                 // yeni hədd, yeni b olur
    }
    cout << "\n";
}
