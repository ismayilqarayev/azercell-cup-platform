"""Python testləri, 1–5-ci mövzular. Bütün suallar orijinaldır; cavablar kodu icra edərək hesablanır (bax: lib.py)."""
from lib import inp, num, out, pick, quiz

DESC = 'DİM qəbul proqramındakı mövzuya uyğun orijinal tapşırıqlar (rəsmi imtahan sualları deyil). Cavabları kodu kağızda izləyərək tapın.'


def test1():
    return quiz('Python 1. Dəyişənlər, tiplər və giriş-çıxış', DESC + ' Mövzu: dəyişənlər, verilənlərin tipləri, input və print.', [
        out('''
            a = 5
            b = 3
            a = a + b
            b = a - b
            print(a, b)
        ''', wrong=['8 3', '8 -3', '5 3', '3 5']),
        pick('Aşağıdakı ifadələrdən hansının nəticəsi float tipindədir?',
             ['7 // 2', '3 * 4', '2 ** 3', '10 / 5', 'abs(-3)'], lambda v: isinstance(v, float),
             why='Python-da / əməliyyatı bölünən tam ədədlər üçün də həmişə float qaytarır.'),
        out('''
            x = "5"
            y = "3"
            print(x + y)
        ''', wrong=['8', '5 3', '15', 'x + y'], why='Dırnaq içindəki 5 və 3 sətirdir; sətirlərdə + birləşdirmə deməkdir.'),
        num('''
            x = 7
            y = 2
            z = x // y + x % y * x
            print(z)
        '''),
        out('''
            a = int(input())
            b = int(input())
            c = a + b
            print(c, a * b, sep=";")
        ''', stdin=['6', '7'], note='Klaviaturadan ardıcıl olaraq 6 və 7 daxil edilir.',
            wrong=['13 42', '42;13', '67;42', '1342']),
        out('''
            n = input()
            print(n * 2)
            print(int(n) * 2)
        ''', stdin=['12'], note='Klaviaturadan 12 daxil edilir.',
            wrong=['24\n24', '1212\n1212', '12\n24', '24\n1212'],
            why='input() həmişə sətir qaytarır: sətri 2-yə vurmaq onu təkrarlayır, int-ə çevirəndən sonra isə ədəd vurulur.'),
        pick('Aşağıdakılardan hansı Python-da dəyişən adı ola BİLMƏZ?',
             ['_sum', 'ad2', '2ad', 'Yaş', 'a_b'], lambda s: not s.isidentifier(), mode='text',
             label=lambda e, v, ok: '%s  ->  %s' % (e, 'keçərsiz ad' if ok else 'keçərli ad'),
             why='Dəyişən adı rəqəmlə başlaya bilməz; hərf, rəqəm və _ işlədilə bilər.'),
        out('''
            x = 10
            x += 5
            x *= 2
            x -= 4
            print(x)
        ''', wrong=['30', '22', '16', '36']),
        out('''
            print("a", "b", "c", sep="-")
            print("x", end="")
            print("y")
        ''', wrong=['a-b-c\nx\ny', 'a b c\nxy', 'a-b-c\nx y', 'abc\nxy'],
            why='sep ardıcıl çıxarılan qiymətlər arasına qoyulan ayırıcıdır, end isə çıxışın sonuna əlavə olunur (standart: sətir keçidi).'),
        out('''
            a = 3
            b = 4.0
            c = a + b
            print(c)
            print(type(c))
        ''', wrong=["7\n<class 'int'>", "7.0\n<class 'int'>", "7\n<class 'float'>", "7.0\n<class 'str'>"],
            why='int və float birlikdə hesablananda nəticə float olur.'),
    ])


def test2():
    return quiz('Python 2. Ədədlər və riyazi əməllər', DESC + ' Mövzu: //, %, **, əməliyyat sırası, ədədin rəqəmləri.', [
        out('''
            print(17 // 5, 17 % 5, 17 / 5)
        ''', wrong=['3 2 3', '3 3 3.4', '4 2 3.4', '3 2 3.0'],
            why='17 // 5 tam hissəni (3), 17 % 5 qalığı (2), 17 / 5 isə kəsr nəticəni (3.4) verir.'),
        num('''
            n = 4729
            print(n // 100 % 10)
        ''', why='n // 100 ədədin son iki rəqəmini atır (47), % 10 isə qalan ədədin son rəqəmini verir.'),
        num('''
            print(2 + 3 * 4 ** 2 // 8 - 1)
        ''', why='Sıra: **, sonra * və // (soldan sağa), sonra + və -.'),
        pick('Aşağıdakı ifadələrdən hansının qiyməti 3-dür?',
             ['10 // 3', '10 % 4', '10 / 4', '2 ** 2', '9 % 5'], lambda v: v == 3),
        out('''
            a = -7
            print(a // 2, a % 2, abs(a))
        ''', wrong=['-3 -1 7', '-4 -1 7', '-3 1 7', '-4 1 -7'],
            why='Mənfi ədədlərdə // aşağıya yuvarlaqlaşdırır: -7 // 2 = -4, qalıq isə həmişə bölənin işarəsini daşıyır: -7 % 2 = 1.'),
        num('''
            n = 5832
            s = 0
            while n > 0:
                s += n % 10
                n //= 10
            print(s)
        ''', why='Dövr hər addımda ədədin son rəqəmini cəmə əlavə edir və onu atır.'),
        inp('''
            n = int(input())
            print(n // 10 + n % 10)
        ''', 13, 10, 99, which='max', domain='iki rəqəmli natural ədəd', check_bound=False,
            why='n // 10 onlar rəqəmini, n % 10 isə təklər rəqəmini verir: proqram rəqəmlərin cəmini tapır.'),
        out('''
            x = 17
            y = 5
            print(x / y, x // y, x % y, x ** 2 % y)
        ''', wrong=['3.4 3 2 2', '3 3 2 4', '3.4 3 2 5', '3.4 3 3 4']),
        out('''
            print(round(3.7), int(3.7), int(-3.7), round(-3.2))
        ''', wrong=['4 3 -4 -3', '3 3 -3 -3', '4 4 -3 -3', '4 3 -3 -4'],
            why='int() kəsr hissəni atır (sıfıra doğru), round() isə ən yaxın tam ədədi verir.'),
        num('''
            print(abs(-5) + 10 % 4 * 3 - 2 ** 3)
        ''', why='abs(-5) = 5; 10 % 4 * 3 = 2 * 3 = 6; 2 ** 3 = 8; deməli 5 + 6 - 8.'),
    ])


def test3():
    return quiz('Python 3. Şərt operatorları', DESC + ' Mövzu: if, elif, else, məntiqi əməliyyatlar.', [
        out('''
            x = 8
            if x > 5:
                print("A")
            if x > 7:
                print("B")
            elif x > 2:
                print("C")
            else:
                print("D")
        ''', wrong=['A', 'A\nC', 'B', 'A\nB\nC'],
            why='Hər ayrıca if müstəqil yoxlanır; elif/else isə yalnız öz if-indən sonra, onun şərti ödənməyəndə işləyir.'),
        out('''
            a = 4
            b = 9
            if a > 3 and b < 5:
                print(1)
            elif a > 3 or b < 5:
                print(2)
            else:
                print(3)
        ''', wrong=['1', '3', '1\n2', '2\n3']),
        pick('Aşağıdakı ifadələrdən hansının qiyməti True-dur?',
             ['5 > 3 and 2 > 4', 'not (3 < 5)', '10 // 3 == 3 and 10 % 3 == 1', '5 != 5', 'not True or False'],
             lambda v: v is True),
        num('''
            n = 27
            if n % 2 == 0:
                r = n // 2
            else:
                r = 3 * n + 1
            print(r)
        '''),
        inp('''
            n = int(input())
            if n % 3 == 0 and n % 5 == 0:
                print("A")
            elif n % 3 == 0:
                print("B")
            else:
                print("C")
        ''', 'B', 10, 99, which='min', check_bound=False,
            q='Klaviaturadan iki rəqəmli natural n ədədi daxil edilir. Proqramın ekrana B çıxarması üçün n-in ən kiçik qiyməti neçədir?'),
        out('''
            a = 9
            b = 4
            if a > b:
                a, b = b, a
            if a + b > 12:
                print(b - a)
            else:
                print(a * b)
        ''', wrong=['36', '13', '4', '9']),
        out('''
            x = 12
            y = 5
            if x > y:
                if x % y == 2:
                    print("A")
                else:
                    print("B")
            else:
                print("C")
        ''', wrong=['B', 'C', 'A\nB', '2']),
        num('''
            year = 2100
            if year % 400 == 0 or (year % 4 == 0 and year % 100 != 0):
                print(366)
            else:
                print(365)
        ''', why='2100 ili 4-ə bölünür, lakin 100-ə də bölünür və 400-ə bölünmür: sıçrayış il deyil.'),
        out('''
            print(7 > 3, 7 == 3, not 7 > 3, 7 > 3 and 3 > 7)
        ''', wrong=['True False True False', 'True True False False', 'False False True False', 'True False False True'],
            why='7 > 3 True; 7 == 3 False; not 7 > 3 = not True = False; 7 > 3 and 3 > 7 = True and False = False.'),
        out('''
            s = 74
            if s >= 90:
                g = 5
            elif s >= 70:
                g = 4
            elif s >= 50:
                g = 3
            else:
                g = 2
            print(g, s // 10)
        ''', wrong=['3 7', '4 74', '4 8', '5 7']),
    ])


def test4():
    return quiz('Python 4. for dövrü və range', DESC + ' Mövzu: for, range(başla, bitir, addım), dövrdə cəm və hasil.', [
        num('''
            s = 0
            for i in range(1, 6):
                s += i
            print(s)
        ''', why='range(1, 6) 1, 2, 3, 4, 5 ədədlərini verir (son ədəd daxil deyil).'),
        out('''
            for i in range(2, 11, 3):
                print(i, end=" ")
        ''', wrong=['2 5 8 11', '3 6 9', '2 4 6 8 10', '2 3 4 5 6 7 8 9 10'],
            why='range(2, 11, 3) 2-dən başlayır, hər dəfə 3 artır və 11-ə çatmamış dayanır.'),
        num('''
            p = 1
            for i in range(1, 6):
                p *= i
            print(p)
        '''),
        out('''
            for i in range(5, 0, -2):
                print(i)
        ''', wrong=['5\n3\n1\n-1', '5\n4\n3\n2\n1', '4\n2', '5\n3']),
        num('''
            c = 0
            for i in range(1, 31):
                if i % 4 == 0:
                    c += 1
            print(c)
        '''),
        out('''
            s = 0
            for i in range(1, 8):
                if i % 2 == 0:
                    s -= i
                else:
                    s += i
            print(s)
        ''', wrong=['28', '-4', '16', '0']),
        inp('''
            n = int(input())
            s = 0
            for i in range(1, n + 1):
                s += i * i
            print(s > 100)
        ''', 'True', 1, 50, which='min', domain='natural ədəd',
            why='n = 6 üçün kvadratlar cəmi 91, n = 7 üçün 140 olur: 100-ü ilk dəfə 7 keçir.'),
        out('''
            s = ""
            for ch in "python":
                s = ch + s
            print(s)
        ''', wrong=['python', 'pytho', 'nohty', 'p y t h o n']),
        out('''
            for i in range(1, 4):
                print(i * "*")
        ''', wrong=['*\n*\n*', '***\n**\n*', '*\n**', '**\n***\n****']),
        num('''
            t = 0
            for i in range(10, 0, -3):
                t += i
            print(t)
        ''', why='range(10, 0, -3) 10, 7, 4, 1 ədədlərini verir.'),
    ])


def test5():
    return quiz('Python 5. while dövrü, break və continue', DESC + ' Mövzu: while, break, continue, ədədin rəqəmləri ilə işləmək.', [
        num('''
            n = 1
            while n < 100:
                n *= 3
            print(n)
        '''),
        num('''
            i = 0
            s = 0
            while i < 10:
                i += 1
                if i % 3 == 0:
                    continue
                s += i
            print(s)
        ''', why='continue cari addımın qalan hissəsini atıb dövrün növbəti addımına keçir: 3, 6 və 9 cəmə əlavə olunmur.'),
        out('''
            for i in range(1, 10):
                if i * i > 30:
                    break
                print(i, end=" ")
        ''', wrong=['1 2 3 4 5 6', '1 2 3 4', '1 4 9 16 25', '6']),
        num('''
            n = 7329
            c = 0
            while n > 0:
                n //= 10
                c += 1
            print(c)
        '''),
        out('''
            a, b = 48, 18
            while b != 0:
                a, b = b, a % b
            print(a)
        ''', wrong=['18', '12', '3', '0'], why='Bu, Evklid alqoritmidir: iki ədədin ən böyük ortaq bölənini tapır.'),
        inp('''
            n = int(input())
            c = 0
            while n > 0:
                n //= 10
                c += 1
            print(c)
        ''', 3, 1, 5000, which='max', domain='natural ədəd', why='Proqram natural ədədin rəqəmlərinin sayını tapır.'),
        out('''
            x = 100
            k = 0
            while x > 1:
                x //= 2
                k += 1
            print(k, x)
        ''', wrong=['7 0', '6 0', '7 1', '5 3']),
        out('''
            s = 0
            i = 1
            while True:
                s += i
                if s > 20:
                    break
                i += 1
            print(i, s)
        ''', wrong=['5 15', '6 20', '7 28', '6 15']),
        num('''
            n = 12345
            r = 0
            while n > 0:
                r = r * 10 + n % 10
                n //= 10
            print(r)
        ''', why='Dövr ədədin rəqəmlərini tərsinə yığır.'),
        out('''
            i = 10
            while i > 0:
                i -= 3
                if i == 4:
                    continue
                print(i, end=" ")
        ''', wrong=['7 4 1', '7 1', '7 4 1 -2', '10 7 1 -2']),
    ])
