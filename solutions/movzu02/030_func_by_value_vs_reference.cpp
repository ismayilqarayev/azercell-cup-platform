#include <bits/stdc++.h>
using namespace std;

void artirDeyerle(int x){
    x += 10;
}

void artirReferansla(int &x){
    x += 10;
}

int main(){
    int a, b;
    cin >> a >> b;

    artirDeyerle(a);
    artirReferansla(b);

    cout << a << " " << b << "\n";
}
