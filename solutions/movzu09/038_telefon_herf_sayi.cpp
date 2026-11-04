#include <bits/stdc++.h>
using namespace std;
string digits;
vector<string> letters = {"","","abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
long long cnt = 0;
void bt(int idx){
    if (idx == (int)digits.size()) { cnt++; return; }
    string& opts = letters[digits[idx] - '0'];
    for (char c : opts) bt(idx + 1);
}
int main(){
    cin >> digits;
    bt(0);
    cout << cnt << "\n";
}
