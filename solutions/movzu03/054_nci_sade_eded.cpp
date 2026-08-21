#include <bits/stdc++.h>
using namespace std;

int main(){
    int k;
    cin >> k;

    int cnt = 0;
    long long num = 1;
    while (cnt < k) {
        num++;
        bool isPrime = true;
        for (long long i = 2; i * i <= num; i++) {
            if (num % i == 0) { isPrime = false; break; }
        }
        if (isPrime) cnt++;
    }

    cout << num << "\n";
}
