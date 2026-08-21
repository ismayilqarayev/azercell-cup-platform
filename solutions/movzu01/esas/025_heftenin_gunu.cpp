#include <bits/stdc++.h>
using namespace std;

int main(){
    int k;                    // Günün nömrəsi (1 = Bazar ertəsi, ..., 7 = Bazar)
    cin >> k;

    // Bütün gün adlarını massivdə (siyahıda) saxlayırıq. Massivin indeksi
    // 0-dan başladığı üçün "Bazar ertəsi" 0-cı yerdə, "Bazar" isə 6-cı yerdədir.
    string names[7] = {"Bazar ertesi", "Cersenbe axsami", "Cersenbe",
                        "Cume axsami", "Cume", "Senbe", "Bazar"};

    // k dəyəri 1-7 arasındadır, massivin indeksi isə 0-6 — ona görə k-dan 1 çıxırıq.
    cout << names[k - 1] << "\n";
}
