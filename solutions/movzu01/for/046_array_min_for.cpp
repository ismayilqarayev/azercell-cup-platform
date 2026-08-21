#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    long long mn;
    cin >> mn;             // ilk ədədi "indiyədək ən kiçik" kimi götürürük

    // qalan ədədləri oxuyub mn ilə müqayisə edirik — kiçikdirsə mn-i yeniləyirik.
    for (int i = 1; i < n; i++) {
        long long x;
        cin >> x;
        if (x < mn) mn = x;
    }

    cout << mn << "\n";
}
