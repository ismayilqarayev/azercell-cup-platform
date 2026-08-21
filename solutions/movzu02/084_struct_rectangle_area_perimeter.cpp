#include <bits/stdc++.h>
using namespace std;

struct Rectangle {
    long long width, height;
};

int main(){
    Rectangle r;
    cin >> r.width >> r.height;

    cout << r.width * r.height << "\n";
    cout << 2 * (r.width + r.height) << "\n";
}
