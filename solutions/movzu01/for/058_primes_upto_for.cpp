#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    bool first = true;
    // XARİCİ dövr hər namizədi (2-dən n-ə qədər) yoxlayır.
    for (int i = 2; i <= n; i++) {
        bool isPrime = true;   // əvvəlcə i-nin sadə olduğunu güman edirik
        // DAXİLİ dövr 2-dən i-nin özünə qədər bölən axtarır.
        // j*j <= i şərti — j özü-özünə vurulanda i-dən böyük olduqda daha
        // axtarmağa ehtiyac qalmır (bu, proqramı sürətləndirir).
        for (int j = 2; j * j <= i; j++) {
            if (i % j == 0) {
                isPrime = false;   // bölən tapıldı, deməli i sadə deyil
                break;             // artıq axtarmağa ehtiyac yoxdur, dövrdən çıxırıq
            }
        }
        if (isPrime) {
            if (!first) cout << " ";
            cout << i;
            first = false;
        }
    }
    cout << "\n";
}
