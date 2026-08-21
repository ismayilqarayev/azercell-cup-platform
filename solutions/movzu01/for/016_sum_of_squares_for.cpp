#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long s = 0;

    // Hər i üçün i-nin kvadratını (i*i) hesablayıb cəmə əlavə edirik.
    // Beləliklə 1*1 + 2*2 + 3*3 + ... + n*n alınır.
    for (int i = 1; i <= n; i++) {
        s += (long long)i * i;
    }

    cout << s << "\n";
}
