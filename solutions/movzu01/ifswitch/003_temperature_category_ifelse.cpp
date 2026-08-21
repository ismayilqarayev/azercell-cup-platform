#include <bits/stdc++.h>
using namespace std;

int main(){
    int t;
    cin >> t;

    // Şərtləri ardıcıl yoxlayırıq: əvvəlcə "10-dan az"dırmı, yoxdursa
    // "25-dən çox"durmu, ikisi də olmasa aralıqda (mülayim) deməkdir.
    if (t < 10)
        cout << "SOYUQ" << "\n";
    else if (t > 25)
        cout << "ISTI" << "\n";
    else
        cout << "MULAYIM" << "\n";
}
