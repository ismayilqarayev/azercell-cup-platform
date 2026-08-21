#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;             // əvvəlcə neçə ədəd oxuyacağımızı öyrənirik

    long long s = 0;
    // n dəfə dövr edib hər dəfə bir ədəd oxuyuruq (x) və onu dərhal cəmə
    // əlavə edirik — ədədləri ayrıca yadda saxlamağa ehtiyac yoxdur.
    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        s += x;
    }

    cout << s << "\n";
}
