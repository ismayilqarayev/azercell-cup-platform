#include <bits/stdc++.h>
using namespace std;

int main(){
    double x;   // kəsr ədəd (məsələn 9.7)
    cin >> x;

    // (int) yazaraq x-i tam ədədə "cast" edirik (çeviririk). Bu, YUVARLAQLAŞDIRMIR —
    // sadəcə kəsr hissəni ATIR. Mənfi ədədlərdə də sıfıra doğru atma olur
    // (məsələn -4.6 → -4, YOX -5).
    cout << (int)x << "\n";
}
