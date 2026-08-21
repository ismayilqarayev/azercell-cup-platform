#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long s = 0;

    // Hər i üçün i%2 != 0 şərti "i tək ədəddir" deməkdir (2-yə bölünmür).
    // Yalnız tək olanları cəmə əlavə edirik.
    for (int i = 1; i <= n; i++) {
        if (i % 2 != 0) s += i;
    }

    cout << s << "\n";
}
