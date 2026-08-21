#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;              // Yoxlanılacaq ədəd
    cin >> n;

    // Bir ədədin 2-yə qalıqsız bölünüb-bölünmədiyini "%" (qalıq) operatoru ilə tapırıq.
    // n 2-yə bölünəndə qalıq 0 olarsa — ədəd CÜTdür, əks halda TƏKdir.
    if (n % 2 == 0)
        cout << "CUT" << "\n";
    else
        cout << "TAK" << "\n";
}
