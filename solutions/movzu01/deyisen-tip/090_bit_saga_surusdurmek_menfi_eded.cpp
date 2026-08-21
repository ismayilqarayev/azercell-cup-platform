#include <bits/stdc++.h>
using namespace std;

int main(){
    int n, k;   // n mənfi ədəddir
    cin >> n >> k;

    // Mənfi ədədləri sağa sürüşdürəndə kompüter işarəni (mənfiliyi) qoruyur
    // ("arithmetic shift" adlanır). Nəticə, n-i 2^k-ya bölüb AŞAĞIYA
    // (mənfi sonsuzluğa doğru) yuvarlaqlaşdırmaqla eynidir.
    cout << (n >> k) << "\n";
}
