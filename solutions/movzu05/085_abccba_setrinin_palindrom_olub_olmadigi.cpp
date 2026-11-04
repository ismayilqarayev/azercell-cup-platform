#include <bits/stdc++.h>
using namespace std;
int main(){
    string s; cin >> s;
    int left = 0, right = (int)s.size() - 1;
    bool isPal = true;
    while (left < right){
        if (s[left] != s[right]) { isPal = false; break; }
        left++; right--;
    }
    cout << (isPal ? "PALINDROM" : "PALINDROM DEYIL") << "\n";
}
