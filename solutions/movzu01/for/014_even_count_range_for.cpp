#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    long long cnt = 0;   // cüt ədədlərin sayğacı

    // a-dan b-yə qədər hər ədədi növbə ilə yoxlayırıq
    for (long long i = a; i <= b; i++) {
        if (i % 2 == 0) cnt++;   // i 2-yə qalıqsız bölünürsə, cütdür
    }

    cout << cnt << "\n";
}
