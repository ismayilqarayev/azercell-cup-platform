#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // Kompüter ədədləri daxildə 0 və 1-lərdən (bitlərdən) ibarət yazır.
    // & (AND) — hər iki ədədin uyğun bitləri 1-dirsə, nəticə biti 1 olur.
    // | (OR)  — ən azı biri 1-dirsə, nəticə biti 1 olur.
    // ^ (XOR) — bitlər FƏRQLİDİRSƏ, nəticə biti 1 olur.
    cout << (a & b) << "\n";
    cout << (a | b) << "\n";
    cout << (a ^ b) << "\n";
}
