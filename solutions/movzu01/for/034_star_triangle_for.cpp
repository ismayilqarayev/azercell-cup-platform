#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    // XARİCİ dövr sətirləri gəzir (1-dən n-ə qədər).
    for (int i = 1; i <= n; i++) {
        // DAXİLİ dövr hər sətirdə neçə ulduz çap olunacağını idarə edir —
        // i-ci sətirdə düz i ədəd ulduz olmalıdır.
        for (int j = 1; j <= i; j++) {
            cout << "*";
        }
        cout << "\n";   // sətir bitdi, yeni sətrə keçirik
    }
}
