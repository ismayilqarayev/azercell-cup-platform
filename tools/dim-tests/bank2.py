"""Python testləri, 6–10-cu mövzular."""
from lib import inp, num, out, pick, quiz

DESC = 'DİM qəbul proqramındakı mövzuya uyğun orijinal tapşırıqlar (rəsmi imtahan sualları deyil). Cavabları kodu kağızda izləyərək tapın.'


def test6():
    return quiz('Python 6. İç-içə dövrlər', DESC + ' Mövzu: iç-içə for dövrləri, naxışlar, cütlərin sayılması.', [
        num('''
            c = 0
            for i in range(3):
                for j in range(4):
                    c += 1
            print(c)
        ''', why='Xarici dövr 3 dəfə, hər dəfə daxili dövr 4 dəfə işləyir.'),
        out('''
            for i in range(1, 4):
                for j in range(1, i + 1):
                    print(j, end="")
                print()
        ''', wrong=['1\n11\n111', '123\n123\n123', '1\n21\n321', '1\n12\n123\n1234']),
        num('''
            s = 0
            for i in range(1, 4):
                for j in range(1, 4):
                    if i != j:
                        s += i * j
            print(s)
        '''),
        out('''
            for i in range(3):
                for j in range(3):
                    if i == j:
                        print("1", end="")
                    else:
                        print("0", end="")
                print()
        ''', wrong=['111\n111\n111', '011\n101\n110', '100\n100\n100', '001\n010\n100']),
        num('''
            c = 0
            for a in range(1, 6):
                for b in range(a, 6):
                    if a + b == 6:
                        c += 1
            print(c)
        ''', why='Uyğun cütlər: (1, 5), (2, 4), (3, 3). b dövrü a-dan başladığı üçün (5, 1) kimi təkrar cütlər yoxdur.'),
        out('''
            for i in range(1, 4):
                for j in range(1, 4):
                    print(i * j, end=" ")
                print()
        ''', wrong=['1 2 3\n1 2 3\n1 2 3', '1 2 3\n4 5 6\n7 8 9', '1 4 9\n2 8 18\n3 12 27', '1 2 3\n2 4 6']),
        num('''
            c = 0
            for i in range(1, 5):
                for j in range(i, 5):
                    c += 1
            print(c)
        '''),
        out('''
            n = 4
            for i in range(n):
                print(" " * (n - i - 1) + "*" * (2 * i + 1))
        ''', wrong=['*\n**\n***\n****', '*\n***\n*****\n*******', '   *\n  **\n ***\n****', '****\n***\n**\n*']),
    ])


def test7():
    return quiz('Python 7. Sətirlər', DESC + ' Mövzu: indeks, dilim, sətir metodları, sətir üzərində dövr.', [
        out('''
            s = "informatika"
            print(s[0], s[-1], len(s))
        ''', wrong=['i a 10', 'i k 11', 'n a 11', 'i a 12']),
        out('''
            s = "Python"
            print(s[1:4], s[:2], s[3:])
        ''', wrong=['yt Py hon', 'ytho Py on', 'Pyt Py hon', 'yth P hon'],
            why='Dilimdə başlanğıc indeks daxildir, son indeks daxil deyil: s[1:4] 1, 2, 3-cü simvolları verir.'),
        num('''
            s = "abracadabra"
            print(s.count("a"))
        '''),
        out('''
            s = "Salam dünya"
            print(s.upper(), s.lower().replace("a", "o"))
        ''', wrong=['SALAM DÜNYA salam dünya', 'Salam dünya SALAM DÜNYA', 'SALAM DÜNYA solom dunyo', 'salam dünya SALAM DÜNYA']),
        out('''
            s = "a1b2c3"
            t = ""
            for ch in s:
                if ch.isdigit():
                    t += ch
            print(t)
        ''', wrong=['abc', 'a1b2c3', '6', '321']),
        out('''
            w = "kitab"
            print(w * 2, w[::-1], w[1::2])
        ''', wrong=['kitabkitab batik kia', 'kitab kitab batik ia', 'kitabkitab kitab ia', 'kitabkitab batik it']),
        num('''
            s = "2 5 8 11"
            t = 0
            for x in s.split():
                t += int(x)
            print(t)
        ''', why='split() sətri boşluqlara görə hissələrə ayırır, int() hər hissəni ədədə çevirir.'),
        out('''
            s = "Informatika"
            print(s.find("m"), s.find("z"), s.index("a"))
        ''', wrong=['4 -1 5', '5 0 6', '5 -1 10', '6 -1 6']),
        pick('s = "Informatika" olarsa, hansı ifadə "tik" sətrini verir?',
             ['s[7:10]', 's[7:9]', 's[-3:]', 's[6:9]', 's[8:11]'], lambda v: v == 'tik',
             setup='s = "Informatika"',
             why='Sətrin indeksləri 0-dan başlayır: I0 n1 f2 o3 r4 m5 a6 t7 i8 k9 a10. Dilimdə son indeks daxil olmur.'),
        num('''
            s = "Salam"
            c = 0
            for ch in s:
                if ch in "aeiou":
                    c += 1
            print(c)
        '''),
    ])


def test8():
    return quiz('Python 8. Siyahılar', DESC + ' Mövzu: indeks və dilim, append/pop/insert/sort, dövrlə siyahı emalı.', [
        out('''
            a = [4, 8, 15, 16]
            a.append(23)
            a.pop(0)
            print(a, len(a))
        ''', wrong=['[4, 8, 15, 16, 23] 5', '[8, 15, 16, 23] 5', '[4, 8, 15, 16] 4', '[8, 15, 16] 3']),
        num('''
            a = [3, 1, 4, 1, 5, 9]
            print(sum(a) - max(a) + min(a))
        '''),
        out('''
            a = [5, 2, 7]
            a.sort()
            b = a[::-1]
            print(a, b)
        ''', wrong=['[5, 2, 7] [7, 2, 5]', '[2, 5, 7] [2, 5, 7]', '[7, 5, 2] [2, 5, 7]', '[2, 5, 7] [7, 2, 5]']),
        num('''
            a = [2, 4, 6, 8, 10]
            s = 0
            for i in range(len(a)):
                if i % 2 == 0:
                    s += a[i]
            print(s)
        ''', why='Şərt elementin yox, İNDEKSİN cüt olmasını yoxlayır: a[0], a[2], a[4] toplanır.'),
        out('''
            a = [1, 2, 3, 4, 5, 6]
            print(a[1:4], a[-2:], a[::2])
        ''', wrong=['[2, 3, 4, 5] [5, 6] [1, 3, 5]', '[2, 3, 4] [4, 5] [1, 3, 5]', '[1, 2, 3] [5, 6] [2, 4, 6]', '[2, 3, 4] [5, 6] [2, 4, 6]']),
        out('''
            a = [1, 2, 3, 4, 5]
            for i in range(len(a) - 1):
                a[i] = a[i + 1]
            print(a)
        ''', wrong=['[2, 3, 4, 5, 1]', '[2, 3, 4, 5, 6]', '[1, 1, 2, 3, 4]', '[2, 2, 3, 4, 5]']),
        num('''
            a = [7, 3, 9, 3, 7, 3]
            print(a.count(3) * 10 + a.index(9))
        '''),
        out('''
            a = list(map(int, "5 3 8".split()))
            b = []
            for x in a:
                b.insert(0, x * 2)
            print(b)
        ''', wrong=['[10, 6, 16]', '[5, 3, 8]', '[16, 10, 6]', '[8, 3, 5]'],
            why='insert(0, ...) hər yeni elementi siyahının əvvəlinə qoyur, ona görə sıra tərsinə dönür.'),
        out('''
            a = [3, 8, 1, 9, 4]
            m = a[0]
            for x in a:
                if x > m:
                    m = x
            print(m, a.index(m))
        ''', wrong=['9 4', '8 1', '9 9', '3 0']),
        num('''
            a = [1, 2, 3]
            b = a
            b.append(4)
            c = a[:]
            c.append(5)
            print(len(a) * 10 + len(c))
        ''', why='b = a eyni siyahıya ikinci ad verir (kopya yaratmır), a[:] isə yeni siyahı yaradır.'),
    ])


def test9():
    return quiz('Python 9. Funksiyalar', DESC + ' Mövzu: def, parametrlər, return, susmaya görə qiymət, lokal dəyişən, rekursiya.', [
        num('''
            def f(x):
                return x * x + 1
            print(f(3) + f(2))
        ''', why='f(3) = 3 * 3 + 1 = 10, f(2) = 2 * 2 + 1 = 5; cəmi 15.'),
        out('''
            def g(a, b=2):
                return a ** b
            print(g(3), g(2, 5), g(b=3, a=2))
        ''', wrong=['9 32 6', '6 10 6', '9 25 9', '9 32 9'],
            why='g(3) = 3 ** 2 = 9 (b susmaya görə 2); g(2, 5) = 2 ** 5 = 32; g(b=3, a=2) = 2 ** 3 = 8.'),
        out('''
            def swap_sum(a, b):
                a, b = b, a + b
                return a, b
            x, y = swap_sum(3, 4)
            print(x, y)
        ''', wrong=['3 7', '7 4', '4 3', '3 4']),
        num('''
            def cnt(n):
                c = 0
                while n > 0:
                    c += n % 2
                    n //= 2
                return c
            print(cnt(13))
        ''', why='Funksiya ədədin ikilik yazılışındakı vahidlərin sayını tapır: 13 = 1101.'),
        out('''
            def f(n):
                if n <= 1:
                    return 1
                return n * f(n - 1)
            print(f(5), f(1))
        ''', wrong=['24 1', '120 0', '15 1', '120 120'],
            why='f(5) = 5 * f(4) = 5 * 4 * f(3) = ... = 5 * 4 * 3 * 2 * 1 = 120; f(1) şərtə görə 1 qaytarır.'),
        out('''
            x = 10
            def h():
                x = 5
                return x * 2
            print(h(), x)
        ''', wrong=['10 5', '5 10', '50 10', '10 50'],
            why='Funksiyanın içindəki x lokal dəyişəndir, xaricdəki x-ə toxunmur.'),
        num('''
            def mx(a, b):
                if a > b:
                    return a
                return b
            print(mx(mx(3, 9), mx(7, 4)))
        ''', why='mx(3, 9) = 9, mx(7, 4) = 7, sonra mx(9, 7) = 9.'),
        out('''
            def add(lst, v):
                lst.append(v)
                return len(lst)
            a = [1]
            print(add(a, 5), add(a, 6), a)
        ''', wrong=['2 3 [1]', '1 2 [1, 5, 6]', '2 3 [1, 5]', '2 2 [1, 5, 6]'],
            why='Siyahı funksiyaya istinad ilə ötürülür: funksiya içindəki dəyişiklik xaricdəki siyahıda da görünür.'),
        num('''
            def s(n):
                return n if n < 10 else n % 10 + s(n // 10)
            print(s(9876))
        ''', why='s(9876) = 6 + s(987) = 6 + 7 + s(98) = 6 + 7 + 8 + s(9) = 6 + 7 + 8 + 9. Funksiya rəqəmlərin cəmini tapır.'),
        out('''
            def f(a):
                a = a + 1
                return a
            n = 5
            m = f(n)
            print(n, m, f(f(n)))
        ''', wrong=['6 6 7', '5 6 6', '6 7 8', '5 5 5']),
    ])


def test10():
    return quiz('Python 10. Alqoritmlər', DESC + ' Mövzu: sayma, axtarış, ən böyük/kiçik, sıralama, sadə ədəd, say sistemləri.', [
        num('''
            a = [4, 9, 2, 7, 5]
            m = sum(a) / len(a)
            c = 0
            for x in a:
                if x > m:
                    c += 1
            print(c)
        ''', why='Orta qiymət 5.4-dür; ondan böyük olanlar 9 və 7-dir.'),
        out('''
            a = [5, 1, 4, 2]
            for i in range(len(a) - 1):
                if a[i] > a[i + 1]:
                    a[i], a[i + 1] = a[i + 1], a[i]
            print(a)
        ''', wrong=['[1, 2, 4, 5]', '[1, 5, 4, 2]', '[1, 4, 5, 2]', '[5, 1, 4, 2]'],
            why='Bu, qabarcıq sıralamasının YALNIZ bir keçididir: ən böyük element sona çıxır, siyahı hələ tam sıralanmır.'),
        num('''
            c = 0
            for n in range(2, 21):
                p = True
                for d in range(2, n):
                    if n % d == 0:
                        p = False
                if p:
                    c += 1
            print(c)
        ''', why='20-yə qədər sadə ədədlər: 2, 3, 5, 7, 11, 13, 17, 19.'),
        out('''
            a = [6, 2, 9, 4, 9]
            k = -1
            for i in range(len(a)):
                if a[i] == 9:
                    k = i
                    break
            print(k)
        ''', mut=[('break', 'pass')], wrong=['-1', '9', '3'],
            why='break dövrü ilk tapılan 9-da dayandırır (indeks 2); onsuz dövr sona qədər işləyib k-nı 4 edərdi.'),
        num('''
            a, b = 1, 1
            for i in range(6):
                a, b = b, a + b
            print(a)
        ''', why='Bu, Fibonaççi ardıcıllığıdır: 1, 1, 2, 3, 5, 8, 13, ...'),
        out('''
            a = [8, 3, 5, 3, 9]
            m = 0
            for i in range(1, len(a)):
                if a[i] < a[m]:
                    m = i
            print(m, a[m])
        ''', wrong=['3 3', '1 8', '0 8', '4 9']),
        num('''
            n = 37
            s = ""
            while n > 0:
                s = str(n % 2) + s
                n //= 2
            print(s)
        ''', why='Qalıqlar sondan əvvələ doğru yazılır: bu, 37-nin ikilik yazılışıdır.'),
        out('''
            a = [2, 5, 1, 4]
            for i in range(1, len(a)):
                a[i] += a[i - 1]
            print(a)
        ''', wrong=['[2, 5, 1, 4]', '[2, 7, 6, 5]', '[2, 7, 8, 9]', '[3, 6, 5, 8]']),
        inp('''
            n = int(input())
            c = 0
            for d in range(1, n + 1):
                if n % d == 0:
                    c += 1
            print(c)
        ''', 2, 10, 99, which='max', domain='iki rəqəmli natural ədəd', check_bound=False,
            why='Proqram n-in bölənlərinin sayını tapır; yalnız sadə ədədin iki böləni olur.'),
        out('''
            a = [1, 2, 3, 4, 5]
            i, j = 0, 3
            while i < j:
                a[i], a[j] = a[j], a[i]
                i += 1
                j -= 1
            print(a)
        ''', wrong=['[5, 4, 3, 2, 1]', '[1, 2, 3, 4, 5]', '[4, 2, 3, 1, 5]', '[2, 1, 3, 4, 5]'],
            why='j = 3 olduğu üçün yalnız ilk dörd element tərsinə çevrilir, sonuncu (5) yerində qalır.'),
    ])
