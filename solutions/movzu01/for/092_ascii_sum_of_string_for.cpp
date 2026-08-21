#include <bits/stdc++.h>
using namespace std;

int main(){
    string s;
    cin >> s;

    long long sum = 0;
    // Hər simvolun kompüterdəki "rəqəm qarşılığı" (ASCII kodu) var.
    // (int)s[i] həmin simvolu bu ədədə çevirir. s.size() sətrin uzunluğudur.
    for (size_t i = 0; i < s.size(); i++) {
        sum += (int)s[i];
    }

    cout << sum << "\n";
}
