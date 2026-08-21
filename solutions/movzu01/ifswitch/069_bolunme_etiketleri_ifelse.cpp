#include <bits/stdc++.h>
using namespace std;

int main(){
    long long n;
    cin >> n;

    bool any = false;   // heç bir şərt doğru olmadısa xəbərdar olmaq üçün bayraq

    // DİQQƏT: burada "else if" YOX, HƏR ŞƏRTİ AYRI-AYRI "if" ilə yoxlayırıq!
    // Çünki bir ədəd eyni anda bir neçə şərtə uyğun ola bilər
    // (məsələn 30 həm 2-yə, həm 3-ə, həm də 5-ə bölünür).
    if (n % 2 == 0) { cout << "2-YE BOLUNUR" << "\n"; any = true; }
    if (n % 3 == 0) { cout << "3-E BOLUNUR" << "\n"; any = true; }
    if (n % 5 == 0) { cout << "5-E BOLUNUR" << "\n"; any = true; }

    if (!any) cout << "HEC BIRINE" << "\n";
}
