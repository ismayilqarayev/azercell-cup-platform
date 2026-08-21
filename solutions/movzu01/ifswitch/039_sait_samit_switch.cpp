#include <bits/stdc++.h>
using namespace std;

int main(){
    char c;
    cin >> c;

    // Sait hərflərin hamısını bir yerdə (ard-arda, break olmadan) yazırıq —
    // hansı sait gəlsə də eyni "SAIT" nəticəsinə düşür (fallthrough deyilən üsul).
    switch (c) {
        case 'a': case 'e': case 'i': case 'o': case 'u':
            cout << "SAIT" << "\n";
            break;
        default:
            // sait olmayan bütün digər hərflər buraya düşür
            cout << "SAMIT" << "\n";
    }
}
