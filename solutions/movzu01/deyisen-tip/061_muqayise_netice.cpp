#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // Müqayisə operatorları (>, <, ==) C++-da "bool" (doğru/yanlış) qaytarır.
    // cout-a çap edərkən bool avtomatik 1 (doğru) və ya 0 (yanlış) kimi göstərilir.
    cout << (a > b) << "\n";
    cout << (a < b) << "\n";
    cout << (a == b) << "\n";
}
