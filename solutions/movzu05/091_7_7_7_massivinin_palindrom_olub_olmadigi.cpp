#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    int left = 0, right = n - 1;
    bool isPal = true;
    while (left < right){
        if (a[left] != a[right]) { isPal = false; break; }
        left++; right--;
    }
    cout << (isPal ? "PALINDROM" : "PALINDROM DEYIL") << "\n";
}
