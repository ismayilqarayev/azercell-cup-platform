#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    // Ədədləri sadə massivdə (yəni "qutucuqlar sırası") saxlayırıq.
    // 100000 ölçüsü kifayət qədər böyükdür ki, bütün n ədəd sığsın.
    long long a[100000];
    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    // İndi massivi SONDAN (indeks n-1) BAŞA (indeks 0) doğru gəzib çap edirik —
    // bu, ədədləri tərs sırada göstərir.
    for (int i = n - 1; i >= 0; i--) {
        cout << a[i];
        if (i > 0) cout << " ";
    }
    cout << "\n";
}
