#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    // ~ (bitwise NOT) ədədin BÜTÜN bitlərini tərsinə çevirir (0→1, 1→0).
    // Kompüterlərin ədədləri saxlama üsulu (ikinin tamamlayıcısı) səbəbindən
    // bunun nəticəsi HƏMİŞƏ -n-1-ə bərabərdir (məs. ~5 = -6, ~0 = -1).
    cout << ~n << "\n";
}
