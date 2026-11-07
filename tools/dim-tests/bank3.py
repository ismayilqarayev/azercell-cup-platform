"""Qarışıq sınaq: DİM informatika blokunun quruluşuna uyğun 22 qapalı + 5 kodlu tapşırıq (yazılı tapşırıqlar yoxdur)."""
from lib import inp, num, out, pick, quiz


def mixed():
    closed = [
        out('''
            a = 12
            b = 5
            print(a // b * b + a % b == a)
        ''', wrong=['False', '12', '2', 'Xəta baş verəcək'],
            why='Bu, bölmənin əsas bərabərliyidir: bölünən = qismət * bölən + qalıq.'),
        out('''
            x = 3
            y = x
            x = x + 2
            y = y * x
            print(x, y)
        ''', wrong=['5 6', '5 5', '3 15', '3 9']),
        out('''
            s = "2024"
            print(int(s) // 10 + len(s))
        '''),
        pick('Aşağıdakı ifadələrdən hansının qiyməti 14-dür?',
             ['(2 + 3) * 4', '2 ** 3 * 2', '50 % 7 * 3', '3 * 4 + 2 ** 2', '2 + 3 * 4'], lambda v: v == 14),
        out('''
            for i in range(4):
                for j in range(i):
                    print("#", end="")
                print(i)
        ''', wrong=['0\n1\n2\n3', '#\n##\n###\n####', '#0\n##1\n###2', '0\n#1\n##2\n###3\n####4']),
        out('''
            n = 10
            s = 0
            while n > 0:
                s += n
                n -= 3
            print(s)
        '''),
        out('''
            a = [3, 6, 9, 12]
            print(a[len(a) // 2], a[-3], sum(a[1:3]))
        ''', wrong=['12 9 18', '9 9 15', '6 3 9', '9 6 18']),
        out('''
            def f(x, y):
                return x - y
            print(f(10, 3), f(y=10, x=3))
        ''', wrong=['7 7', '-7 7', '7 13', '13 -7'],
            why='Açar sözlü çağırışda f(y=10, x=3) ilə x = 3, y = 10 olur.'),
        out('''
            s = "informatika"
            print(s[2:6].upper(), s.count("i"))
        ''', wrong=['FORMA 2', 'FORM 1', 'NFOR 2', 'FORM 3']),
        pick('Aşağıdakı ifadələrdən hansının tipi sətirdir (str)?',
             ['str(5) + str(3)', '5 + 3', 'len("ab")', '"ab" == "ab"', 'int("7") * 2'], lambda v: isinstance(v, str)),
        out('''
            a = 7
            b = 2
            if a % b == 0:
                print("cüt")
            else:
                print(a // b)
        ''', wrong=['cüt', '1', '3.5', '2']),
        out('''
            x = 5
            while x > 0:
                x -= 2
                print(x)
        ''', wrong=['5\n3\n1', '3\n1', '3\n1\n-1\n-3', '4\n2\n0']),
        inp('''
            n = int(input())
            print(n % 7 + n // 7)
        ''', 6, 1, 100, which='max', domain='natural ədəd',
            why='n = 7q + r yazılışında proqram q + r cəmini çıxarır.'),
        out('''
            a = [2, 4, 6]
            b = []
            for x in a:
                b.append(x * x)
            print(b[1], sum(b))
        ''', wrong=['4 56', '16 12', '16 36', '4 12']),
        out('''
            s = 0
            for i in range(1, 6):
                if i % 2 == 1:
                    continue
                s += i * i
            print(s)
        ''', wrong=['35', '55', '8', '4']),
        out('''
            print(10 / 4, 10 // 4, 10 % 4, -10 // 4)
        ''', wrong=['2.5 2 2 -2', '2.5 2 2 -2.5', '2 2 2 -3', '2.5 2.0 2 -3'],
            why='10 / 4 = 2.5; 10 // 4 = 2; 10 % 4 = 2; -10 // 4 = -3, çünki // həmişə aşağıya yuvarlaqlaşdırır.'),
        out('''
            a, b = 5, 9
            while a != b:
                if a > b:
                    a -= b
                else:
                    b -= a
            print(a)
        ''', wrong=['5', '9', '4', '0']),
        out('''
            s = "a-b-c"
            print(s.split("-"), s.replace("-", ""))
        ''', wrong=["['a-b-c'] abc", "['a', 'b', 'c'] a b c", "['a', 'b', 'c'] a-b-c", "['abc'] abc"]),
        out('''
            x = "3"
            y = 4
            print(x * y, int(x) * y)
        ''', wrong=['12 12', '3333 3333', '34 12', '3333 7']),
        out('''
            def f(n):
                s = 0
                for i in range(1, n + 1):
                    if n % i == 0:
                        s += i
                return s
            print(f(6), f(7))
        ''', wrong=['6 7', '12 7', '9 8', '12 14'],
            why='f(n) n-in bütün bölənlərinin cəmini tapır: 6 üçün 1 + 2 + 3 + 6 = 12, 7 üçün 1 + 7 = 8.'),
        out('''
            a = [1, 2, 3, 4, 5]
            for i in range(0, len(a), 2):
                a[i] = 0
            print(a)
        ''', wrong=['[0, 0, 0, 0, 0]', '[1, 0, 3, 0, 5]', '[0, 2, 0, 4, 5]', '[0, 2, 3, 4, 5]']),
        out('''
            k = 0
            for i in range(1, 4):
                for j in range(i, 4):
                    k += j
            print(k)
        '''),
    ]
    coded = [
        num('''
            a = 17
            b = 5
            c = a % b * 10 + a // b
            print(c)
        '''),
        num('''
            s = 0
            for i in range(1, 11):
                if i % 3 != 0:
                    s += i
            print(s)
        '''),
        num('''
            a = [5, 3, 8, 1, 9, 2]
            m = 0
            for x in a:
                if x % 2 == 1 and x > m:
                    m = x
            print(m)
        ''', why='Yalnız tək ədədlər nəzərə alınır: 5, 3, 1, 9. Onların ən böyüyü tapılır.'),
        num('''
            def f(x):
                if x < 10:
                    return x
                return f(x // 10) + x % 10
            print(f(7654))
        ''', why='f(7654) = 4 + f(765) = 4 + 5 + f(76) = 4 + 5 + 6 + f(7) = 4 + 5 + 6 + 7: funksiya rəqəmlərin cəmini tapır.'),
        num('''
            n = 97
            k = 0
            while n >= 5:
                n = n // 5 + n % 5
                k += 1
            print(k)
        '''),
    ]
    assert len(closed) == 22 and len(coded) == 5, (len(closed), len(coded))
    return quiz('Python: DİM formatında sınaq (22 qapalı + 5 kodlu)',
                'Qəbul imtahanının informatika blokunun quruluşuna uyğun orijinal sınaq: 22 qapalı və 5 kodlu tapşırıq, bütün Python mövzularını əhatə edir '
                '(rəsmi imtahan sualları deyil, yazılı tapşırıqlar daxil deyil).',
                closed + coded)
