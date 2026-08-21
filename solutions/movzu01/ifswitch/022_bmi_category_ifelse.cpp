#include <bits/stdc++.h>
using namespace std;
int main(){
    double w, h; cin >> w >> h;
    double m = h / 100.0;
    double bmi = w / (m * m);
    if (bmi < 18.5) cout << "ARIQ\n";
    else if (bmi < 25.0) cout << "NORMAL\n";
    else if (bmi < 30.0) cout << "ARTIQ_CEKI\n";
    else cout << "PIYLENME\n";
}
