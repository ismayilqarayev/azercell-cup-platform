#include <bits/stdc++.h>
using namespace std;

struct Point {
    double x, y;
};

int main(){
    Point p1, p2;
    cin >> p1.x >> p1.y;
    cin >> p2.x >> p2.y;

    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    double dist = sqrt(dx * dx + dy * dy);

    cout << fixed << setprecision(2) << dist << "\n";
}
