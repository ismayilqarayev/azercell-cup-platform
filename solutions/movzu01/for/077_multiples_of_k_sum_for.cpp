#include <bits/stdc++.h>
using namespace std;

int main(){
    long long k, n;
    cin >> k >> n;

    long long s = 0;
    // 1-dən n-ə qədər hər ədədi yoxlayırıq; əgər k-ya qalıqsız bölünürsə
    // (yəni k-nın misilidirsə), onu cəmə əlavə edirik.
    for (long long i = 1; i <= n; i++) {
        if (i % k == 0) s += i;
    }

    cout << s << "\n";
}
