#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin >> n;

    // for dövrü ilə i-ni 1-dən 10-a qədər dəyişirik və hər dəfə n*i hesablayıb
    // "n x i = nəticə" formatında çap edirik — bu, vurma cədvəlinin özüdür.
    for (int i = 1; i <= 10; i++) {
        cout << n << " x " << i << " = " << n * i << "\n";
    }
}
