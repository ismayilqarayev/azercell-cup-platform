#include <bits/stdc++.h>
using namespace std;

int main(){
    long long a, b;
    cin >> a >> b;

    // XOR (^) operatorunun maraqlı xüsusiyyəti var: eyni ədədi iki dəfə
    // XOR etsən, ilk dəyər geri qayıdır. Bundan istifadə edərək üçüncü
    // dəyişən olmadan iki ədədi yerdəyişdirmək mümkündür:
    a ^= b;   // a indi "a XOR b"-yə bərabərdir
    b ^= a;   // b = b XOR (a XOR b) = köhnə a  → b artıq köhnə a-dır
    a ^= b;   // a = (a XOR b) XOR (köhnə a) = köhnə b → a artıq köhnə b-dir

    cout << a << " " << b << "\n";
}
