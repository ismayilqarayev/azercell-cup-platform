#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    int cnt = 0;
    // Hər ədədi oxuyuruq, əgər 0-dan böyükdürsə (müsbətdirsə) sayğaca əlavə edirik.
    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        if (x > 0) cnt++;
    }

    cout << cnt << "\n";
}
