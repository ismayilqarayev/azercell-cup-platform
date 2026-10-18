/* Python bölməsinin dərsləri, 1-ci hissə: tanışlıq və dilin əsasları.
   Mətnlər və kod nümunələri bu layihə üçün orijinal yazılıb; sıra pedaqoji məqsədlə düzülüb. Hər dərsin "src" sahəsi
   https://metanit.com/python/tutorial/<src>.php ünvanındakı uyğun dərsə (rus dilində) keçid verir.
   Mətn işarələri: `kod` -> <code>, **qalın** -> <strong>. Kod sətirləri String.raw ilə yazılır (geri-xətt olduğu kimi qalır). */
PY_CHAPTERS.push({
  id: 'giris',
  title: 'Python ilə tanışlıq',
  blurb: 'Dil nədir, necə quraşdırılır, ilk proqramı harada işlədək.',
  lessons: [
    {
      id: 'giris-1', src: '1.1', title: 'Python proqramlaşdırma dili',
      text: [
        'Python 1991-ci ildə Guido van Rossum tərəfindən yaradılıb. Bu, ümumi təyinatlı, yüksək səviyyəli və **interpretator** ilə işləyən dildir: kodu əvvəlcə ayrıca fayla tərcümə etmək lazım deyil, interpretator onu sətir-sətir icra edir.',
        'Dilin əsas xüsusiyyəti oxunaqlı sintaksisdir. Blokları fiqurlu mötərizə yox, **girinti** (sətrin əvvəlindəki boşluqlar) ayırır. Ona görə Python kodu səliqəli yazılmağa məcbur edir və başlayanlar üçün rahatdır.',
        'Python veb tətbiqlərdə, verilənlərin təhlilində, süni intellektdə, avtomatlaşdırmada və təhsildə geniş işlədilir.',
      ],
      code: String.raw`print("Salam, dünya!")`,
      tip: 'Həmişə Python 3 istifadə et. Python 2 çoxdan dəstəklənmir.',
    },
    {
      id: 'giris-2', src: '1.2', lang: 'shell', title: 'Windows-da quraşdırma və ilk proqram',
      text: [
        'python.org saytından Windows üçün son Python 3 quraşdırıcısını yüklə. Quraşdırmanın ilk pəncərəsində **Add python.exe to PATH** xanasını mütləq işarələ, yoxsa komanda sətrindən `python` yazanda sistem proqramı tapmaz.',
        'Quraşdırmanı yoxlamaq üçün komanda sətrini (Win+R, `cmd`) aç və versiyanı soruş. Sonra istənilən mətn redaktorunda `hello.py` faylı yaradıb işlət.',
      ],
      code: String.raw`python --version
python hello.py`,
      tip: 'Windows-da `py` işə salıcısı da var: `py hello.py` və `py -3.12 hello.py` (konkret versiya) işləyir.',
    },
    {
      id: 'giris-3', src: '1.5', lang: 'shell', title: 'macOS-da quraşdırma və ilk proqram',
      text: [
        'macOS-da Python 3-ü python.org-dan yükləyib quraşdıra və ya Homebrew paket menecerindən istifadə edə bilərsən. Terminalda əmr `python3` adı ilə çağırılır.',
      ],
      code: String.raw`brew install python
python3 --version
python3 hello.py`,
    },
    {
      id: 'giris-4', src: '1.6', lang: 'shell', title: 'Linux-da quraşdırma və ilk proqram',
      text: [
        'Linux paylanmalarının çoxunda Python 3 artıq quraşdırılmış olur. Yoxlamaq və lazım olsa quraşdırmaq üçün paket menecerindən istifadə et (aşağıdakı nümunə Debian və Ubuntu üçündür).',
      ],
      code: String.raw`python3 --version
sudo apt install python3 python3-pip python3-venv
python3 hello.py`,
    },
    {
      id: 'giris-5', src: '1.7', lang: 'shell', title: 'Python versiyalarını idarə etmək',
      text: [
        'Bir kompüterdə bir neçə Python versiyası lazım ola bilər: bir layihə 3.11 tələb edir, digəri 3.13. Windows-da bunu `py -3.12` kimi işə salıcı həll edir. macOS və Linux-da isə **pyenv** aləti versiyaları quraşdırıb layihə üzrə seçməyə imkan verir.',
        'Layihələrin bir-birinə mane olmaması üçün hər layihəyə ayrıca **virtual mühit** yaradılır (bu barədə «Standart kitabxana» fəslində danışırıq).',
      ],
      code: String.raw`py -0                 # Windows: quraşdırılmış versiyaların siyahısı
pyenv install 3.12    # macOS/Linux: yeni versiya yüklə
pyenv local 3.12      # cari qovluq üçün həmin versiyanı seç`,
    },
    {
      id: 'giris-6', src: '1.3', title: 'PyCharm-da ilk proqram',
      text: [
        'PyCharm JetBrains şirkətinin Python üçün hazırladığı mühitdir, pulsuz Community versiyası var. Yeni layihə yaradanda interpretatoru (Python versiyasını) seçirsən, sonra layihəyə `main.py` faylı əlavə edib yaşıl **Run** düyməsi ilə işlədirsən.',
        'Mühit kodu tamamlayır, səhvləri yazarkən göstərir və debaqer (addım-addım icra) verir, bu da böyük layihələrdə çox işə yarayır.',
      ],
      code: String.raw`# main.py
name = "Aysel"
print("Salam,", name)`,
    },
    {
      id: 'giris-7', src: '1.4', title: 'Visual Studio-da Python',
      text: [
        'Visual Studio quraşdırıcısında **Python development** iş yükünü seçməklə Python dəstəyi əlavə olunur. Sonra «Python Application» şablonu ilə layihə yaradıb Start düyməsi ilə işlədirsən.',
        'Yüngül alternativ istəyirsənsə, Visual Studio Code və ona əlavə olunan Python genişlənməsi də eyni işi görür, sürətlə açılır və az yaddaş tutur.',
      ],
      code: String.raw`# Visual Studio və ya VS Code-da eyni fayl:
for i in range(3):
    print("Addım", i)`,
    },
  ],
});

PY_CHAPTERS.push({
  id: 'esaslar',
  title: 'Dilin əsasları',
  blurb: 'Dəyişənlər, tiplər, giriş-çıxış, ədədlər, şərtlər və dövrlər.',
  lessons: [
    {
      id: 'esas-1', src: '2.1', title: 'Proqram yazmağa giriş',
      text: [
        'Python proqramı əmrlərin ardıcıllığıdır və yuxarıdan aşağı icra olunur. Hər əmr adətən ayrıca sətirdə yazılır, nöqtəli vergül lazım deyil.',
        '`#` işarəsindən sonrakı hissə **şərhdir**: interpretator onu görmür, oxuyan insan üçün izahdır. Girinti isə sintaksisin hissəsidir: bloka aid sətirlər eyni sayda boşluqla (adətən 4) sürüşdürülür.',
      ],
      code: String.raw`# Bu şərhdir, icra olunmur
print("Başladı")

if True:
    print("Bu sətir girintilidir, if blokuna aiddir")

print("Bitdi")`,
      tip: 'Boşluq və tab-ı qarışdırma. Redaktoru Tab düyməsi 4 boşluq yazacaq şəkildə qur.',
    },
    {
      id: 'esas-2', src: '2.2', title: 'Dəyişənlər və məlumat tipləri',
      text: [
        'Dəyişən dəyərə verilən addır. Python-da tipi əvvəlcədən yazmırsan: ad dəyər aldıqda yaranır (`x = 5`). Tip dəyişənə yox, **dəyərə** aiddir, ona görə eyni ad sonradan başqa tipli dəyər saxlaya bilər.',
        'Əsas tiplər: `int` (tam ədəd, ölçüsü məhdud deyil), `float` (kəsr), `str` (mətn), `bool` (`True` və `False`) və `None` («dəyər yoxdur»). Tipi `type()` ilə yoxlayırıq.',
        'Ad qaydaları: hərflə və ya `_` ilə başlayır, hərflərin böyük-kiçikliyi fərqlidir. Python-da ad `snake_case` üslubunda yazılır (`student_count`).',
      ],
      code: String.raw`age = 15
price = 4.75
name = "Aysel"
is_student = True
nothing = None

print(type(age), type(price), type(name), type(is_student), type(nothing))

a = b = 0          # eyni dəyəri iki dəyişənə
x, y = 3, 4        # bir sətirdə iki dəyişən
age = "on beş"     # eyni ad indi mətn saxlayır`,
      tip: 'Kəsr ədədlər ikilik sistemdə təqribi saxlanılır: `0.1 + 0.2` tam `0.3` etmir. Dəqiq pul hesabı üçün `decimal` modulundan istifadə et.',
    },
    {
      id: 'esas-3', src: '2.5', title: 'Tip çevirmələri',
      text: [
        'Bir tipdən digərinə keçmək üçün tipin adı funksiya kimi çağırılır: `int()`, `float()`, `str()`, `bool()`. Ən çox rast gəlinən hal istifadəçidən gələn mətni ədədə çevirməkdir.',
        'Kəsr ədədi `int()`-ə verəndə hissə **atılır** (yuvarlaqlaşdırılmır): `int(3.9)` nəticəsi `3`-dür. Mətn ədədə oxşamırsa `ValueError` yaranır. Boş sətir, `0`, `0.0`, `None` və boş kolleksiya `bool()`-da `False`, qalan hər şey `True` sayılır.',
      ],
      code: String.raw`print(int("42") + 1)       # 43
print(float("2.5") * 2)     # 5.0
print(str(7) + " gün")      # 7 gün
print(int(3.9), round(3.9)) # 3 4

print(bool(0), bool(""), bool([]))      # False False False
print(bool(5), bool("a"), bool([0]))    # True True True

# int("abc")  ->  ValueError: invalid literal for int()`,
    },
    {
      id: 'esas-4', src: '2.14', title: 'Konsol girişi və çıxışı',
      text: [
        '`print()` istənilən sayda dəyəri çap edir. Aralarına `sep` ilə ayırıcı, sonuna `end` ilə başqa simvol qoya bilərsən (susmaya görə boşluq və sətir sonu).',
        '`input()` istifadəçidən sətir oxuyur və həmişə **mətn** qaytarır. Ədəd lazımdırsa nəticəni çevirmək lazımdır: `int(input())`. Mətnə dəyər qoşmaq üçün ən rahat yol `f"..."` sətirləridir.',
      ],
      code: String.raw`print("a", "b", "c", sep="-")     # a-b-c
print("Yüklənir", end="...")        # sətir sonu əvəzinə ...
print(" hazır")

name = input("Adın nədir? ")
age = int(input("Neçə yaşın var? "))
print(f"Salam, {name}! Gələn il {age + 1} yaşın olacaq.")`,
      task: 'İstifadəçidən iki tam ədəd oxu və onların cəmini, hasilini çap et.',
      answer: String.raw`a = int(input())
b = int(input())
print("Cəm:", a + b)
print("Hasil:", a * b)`,
    },
    {
      id: 'esas-5', src: '2.3', title: 'Ədədlər üzərində arifmetik əməliyyatlar',
      text: [
        'Əməllər: `+`, `-`, `*`, `/` (həmişə `float` qaytarır), `//` (tam bölmə), `%` (qalıq), `**` (qüvvət). Mənfi ədədlərdə tam bölmə aşağı yuvarlaqlaşdırır: `-7 // 2` nəticəsi `-4`-dür və qalıq həmişə bölənin işarəsini daşıyır.',
        'Prioritet riyaziyyatdakı kimidir: əvvəl `**`, sonra `*` `/` `//` `%`, sonra `+` `-`. Şübhə edəndə mötərizə qoy. `x += 1` kimi qısa yazılışlar dəyişəni yeniləyir.',
      ],
      code: String.raw`print(7 / 2, 7 // 2, 7 % 2, 2 ** 10)   # 3.5 3 1 1024
print(-7 // 2, -7 % 2)                  # -4 1

q, r = divmod(17, 5)                    # həm nisbət, həm qalıq
print(q, r)                             # 3 2

x = 10
x += 5      # 15
x *= 2      # 30
print(abs(-3), round(2.675, 2), pow(2, 5))`,
    },
    {
      id: 'esas-6', src: '2.12', title: 'Ədədlər üzərində bit əməliyyatları',
      text: [
        'Bit əməliyyatları ədədin ikilik yazılışının hər biti ilə işləyir: `&` (və), `|` (və ya), `^` (xor), `~` (inversiya), `<<` və `>>` (sola və sağa sürüşdürmə).',
        'Praktik misallar: `n & 1` ədədin təkliyini yoxlayır, `1 << k` ikinin `k`-cı qüvvətini verir, `x >> 1` ikiyə tam bölməyə bərabərdir. İkilik yazılışı `bin()` ilə görmək olar.',
      ],
      code: String.raw`a, b = 5, 3          # 101 və 011
print(a & b, a | b, a ^ b)   # 1 7 6
print(~a)                    # -6
print(1 << 3, 16 >> 2)       # 8 4
print(bin(10), 0b1010)       # 0b1010 10

n = 7
print("tək" if n & 1 else "cüt")`,
    },
    {
      id: 'esas-7', src: '2.4', title: 'Şərt ifadələri',
      text: [
        'Şərt ifadəsi `True` və ya `False` verir. Müqayisə operatorları: `==`, `!=`, `<`, `>`, `<=`, `>=`. Python-da zəncirvari müqayisə mümkündür: `1 < x < 10`.',
        'Məntiqi operatorlar `and`, `or`, `not` yazılışı ilə gedir və **qısa dövrə** ilə işləyir: nəticə artıq məlumdursa ikinci tərəf hesablanmır. `in` mənsubiyyəti, `is` isə eyni obyekt olub-olmadığını yoxlayır (dəyərlərin bərabərliyi üçün `==` işlət).',
      ],
      code: String.raw`x = 7
print(x > 5 and x < 10)     # True
print(5 < x < 10)           # True, eyni məna
print(not x == 7)           # False
print(x in (1, 7, 9))       # True
print("a" in "kitab")       # True

name = ""
print(name or "Naməlum")    # boşdursa ikinci dəyər: Naməlum`,
    },
    {
      id: 'esas-8', src: '2.6', title: 'Şərt konstruksiyası if',
      text: [
        '`if` şərt doğru olanda blokunu icra edir. `elif` əlavə şərtlər, `else` isə heç biri uyğun gəlməyəndə işləyir. Şərtdən sonra `:` yazılır və blok girinti ilə ayrılır.',
        'Sadə seçim üçün qısa yazılış var: `a if şərt else b`. Çoxlu `elif` zəncirində yuxarıdan aşağı yoxlanılır və ilk doğru olan seçilir.',
      ],
      code: String.raw`score = 78

if score >= 90:
    grade = "əla"
elif score >= 70:
    grade = "yaxşı"
elif score >= 50:
    grade = "kafi"
else:
    grade = "qeyri-kafi"
print(grade)                    # yaxşı

status = "keçdi" if score >= 50 else "kəsildi"
print(status)`,
      task: 'Ədəd oxu və onun müsbət, mənfi, yoxsa sıfır olduğunu çap et.',
      answer: String.raw`n = int(input())
if n > 0:
    print("müsbət")
elif n < 0:
    print("mənfi")
else:
    print("sıfır")`,
    },
    {
      id: 'esas-9', src: '2.7', title: 'Dövrlər',
      text: [
        '`while` şərt doğru qaldıqca təkrarlayır, `for` isə ardıcıllığın (siyahı, sətir, `range`) hər elementi üçün işləyir. Say məlumdursa `for`, bitmə şərti məlumdursa `while` seç.',
        '`break` dövrü dərhal bitirir, `continue` cari addımı atıb növbətiyə keçir. Dövrün `else` blokunu `break` ilə kəsilməyəndə icra olunur.',
      ],
      code: String.raw`total = 0
for i in range(1, 6):           # 1, 2, 3, 4, 5
    total += i
print(total)                    # 15

n = 27
steps = 0
while n != 1:                   # Collatz ardıcıllığı
    n = n // 2 if n % 2 == 0 else 3 * n + 1
    steps += 1
print(steps)

for x in [3, 8, 11, 14]:
    if x % 2 == 1:
        print("ilk tək:", x)
        break
else:
    print("tək ədəd yoxdur")`,
      task: '1-dən 10-a qədər vurma cədvəlini (7-yə vurma) çap et: «7 x 3 = 21».',
      answer: String.raw`for i in range(1, 11):
    print(f"7 x {i} = {7 * i}")`,
    },
  ],
});
