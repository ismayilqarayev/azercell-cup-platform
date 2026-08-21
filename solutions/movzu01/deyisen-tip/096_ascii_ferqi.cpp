#include <bits/stdc++.h>
using namespace std;

int main(){
    char c1, c2;
    cin >> c1 >> c2;

    // Hər simvolun daxili (ASCII) kodu var (məs. 'a' = 97). (int) ilə
    // simvolu bu ədədə çeviririk, sonra sadəcə ədədlər kimi çıxırıq.
    cout << (int)c1 - (int)c2 << "\n";
}
