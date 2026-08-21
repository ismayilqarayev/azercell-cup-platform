#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b, c;
    cin >> a >> b >> c;

    // C++-da * və / əməliyyatları + və -dən ÖNCƏ icra olunur (operator
    // üstünlüyü). Mötərizə daxilindəki (a-c) isə hər şeydən əvvəl hesablanır.
    // Beləliklə ifadə bu sırayla işlənir: (a-c) → b*c → /2 → cəmlər.
    cout << a + b * c - (a - c) / 2 << "\n";
}
