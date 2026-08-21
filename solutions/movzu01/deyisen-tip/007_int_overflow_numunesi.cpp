#include <bits/stdc++.h>
using namespace std;

int main(){
    int a, b;   // DİQQƏT: burada bilərəkdən "long long" yox, "int" işlədirik
    cin >> a >> b;

    // int tipi ən çoxu ~2.1 milyarda qədər ədəd tuta bilir. Əgər a+b bu
    // hədddən keçərsə, "daşma (overflow)" baş verir və nəticə mənfiyə
    // "dönür" — bu, real proqramlaşdırmada tez-tez rast gəlinən səhvdir.
    // Böyük ədədlərlə işləyəndə "long long" işlətmək lazımdır ki, bu olmasın.
    int s = a + b;
    cout << s << "\n";
}
