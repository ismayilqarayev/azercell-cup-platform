#include <bits/stdc++.h>
using namespace std;
int main(){
    long long n; cin >> n;
    bool any = false;
    if (n % 2 == 0) { cout << "2-YE BOLUNUR\n"; any = true; }
    if (n % 3 == 0) { cout << "3-E BOLUNUR\n"; any = true; }
    if (n % 5 == 0) { cout << "5-E BOLUNUR\n"; any = true; }
    if (!any) cout << "HEC BIRINE\n";
}
