#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    // Bu dəfə dövr n-dən başlayıb 1-ə qədər AZALARAQ gedir (i--).
    for (int i = n; i >= 1; i--) {
        cout << i;
        if (i > 1) cout << " ";   // sonuncu ədəddən sonra artıq boşluq qoymuruq
    }
    cout << "\n";
}
