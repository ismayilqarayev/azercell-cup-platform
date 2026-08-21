#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n, k;   // n — dairədəki element sayı, k — atılan addım sayı
    cin >> n >> k;

    // K addımdan sonra hansı indeksdə olduğumuzu tapmaq üçün sadəcə
    // k % n hesablamaq kifayətdir — çünki hər n addımdan sonra dairə "bir
    // dövrə" tamamlayıb başlanğıc mövqeyə qayıdır.
    cout << k % n << "\n";
}
