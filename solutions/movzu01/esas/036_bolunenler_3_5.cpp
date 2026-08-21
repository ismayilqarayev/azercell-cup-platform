#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    int cnt = 0;    // 3-ə və ya 5-ə bölünən ədədlərin sayğacı

    // 1-dən n-ə qədər hər ədədi növbə ilə yoxlayırıq
    for (int i = 1; i <= n; i++) {
        // i%3 != 0 deməkdir "3-ə bölünmür", i%5 != 0 deməkdir "5-ə bölünmür".
        // Əgər HƏR İKİSİ doğrudursa (yəni nə 3-ə, nə 5-ə bölünmür), bu ədədi
        // ötürürük (continue) və sayğaca əlavə ETMİRİK.
        if (i % 3 != 0 && i % 5 != 0) continue;
        cnt++;   // buraya çatdıqsa, deməli i 3-ə və ya 5-ə bölünür
    }

    cout << cnt << "\n";
}
