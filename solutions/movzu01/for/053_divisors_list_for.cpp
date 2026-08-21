#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    bool first = true;   // birinci bölənlə növbəti bölənlər arasında boşluq qoymaq üçün
    // 1-dən n-ə qədər hər ədədi (i) yoxlayırıq: əgər n bu ədədə qalıqsız
    // bölünürsə (n%i==0), demək i, n-in bölənidir.
    for (int i = 1; i <= n; i++) {
        if (n % i == 0) {
            if (!first) cout << " ";
            cout << i;
            first = false;
        }
    }
    cout << "\n";
}
