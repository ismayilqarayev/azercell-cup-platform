#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    long long hasil = 1;
    while (n > 0) {
        hasil *= n % 10;
        n /= 10;
    }

    cout << hasil << "\n";
}
