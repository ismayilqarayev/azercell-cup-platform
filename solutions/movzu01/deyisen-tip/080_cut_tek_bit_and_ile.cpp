#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    // Bir ədədin ikilik (binary) yazılışında ən sonuncu bit onun cüt/tək
    // olmasını göstərir: son bit 0-dırsa cütdür, 1-dirsə təkdir.
    // n & 1 məhz bu son biti "təcrid edir" (ayırır).
    if ((n & 1) == 0)
        cout << "CUT" << "\n";
    else
        cout << "TAK" << "\n";
}
