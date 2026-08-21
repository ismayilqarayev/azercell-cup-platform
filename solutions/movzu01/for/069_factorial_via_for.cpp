#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long f = 1;   // vurmanın başlanğıc (neytral) dəyəri

    // 1-dən n-ə qədər hər ədədi f-ə vururuq — nəticədə n! (n faktorial) alınır.
    for (int i = 1; i <= n; i++) {
        f *= i;
    }

    cout << f << "\n";
}
