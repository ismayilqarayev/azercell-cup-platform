#include <bits/stdc++.h>
using namespace std;

int main(){
    long long m;   // alış-veriş məbləği
    cin >> m;

    long long faiz;
    // Məbləğə görə endirim faizini tapırıq (kiçikdən böyüyə doğru yoxlayırıq).
    if (m < 100) faiz = 0;
    else if (m < 500) faiz = 5;
    else if (m < 1000) faiz = 10;
    else faiz = 15;

    // Son qiymət = ilkin məbləğ - (məbləğin faiz%-i qədər hissəsi).
    cout << m - m * faiz / 100 << "\n";
}
