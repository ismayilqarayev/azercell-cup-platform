#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // Adətən iki dəyişəni yerdəyişmək üçün üçüncü ("köməkçi") dəyişənə
    // ehtiyac olur. Burada isə yalnız toplama/çıxma ilə "riyazi trük" edirik:
    a = a + b;   // indi a köhnə a+b-yə bərabərdir
    b = a - b;   // (köhnə a+b) - (köhnə b) = köhnə a  →  b indi köhnə a-dır
    a = a - b;   // (köhnə a+b) - (yeni b, yəni köhnə a) = köhnə b  →  a indi köhnə b-dir

    cout << a << " " << b << "\n";   // artıq yerləri dəyişib
}
