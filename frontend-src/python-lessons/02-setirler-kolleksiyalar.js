/* Python bölməsinin dərsləri, 2-ci hissə: sətirlər və kolleksiyalar (siyahı, kortej, diapazon, lüğət, çoxluq). */
PY_CHAPTERS.push({
  id: 'setirler',
  title: 'Sətirlər',
  blurb: 'Mətnlə işləmək: indeks, kəsim, metodlar və formatlaşdırma.',
  lessons: [
    {
      id: 'set-1', src: '5.1', title: 'Sətirlərlə iş',
      text: [
        'Sətir `\'...\'`, `"..."` və ya üç dırnaqla (`"""..."""`, çoxsətirli) yazılır. `\\n` yeni sətir, `\\t` tab, `\\\\` isə bir əks-xətt verir. `r"..."` ilə yazılan «xam» sətirdə əks-xətt xüsusi məna daşımır.',
        'Sətirdəki simvollara indekslə müraciət olunur: ilk simvol `s[0]`, sonuncu `s[-1]`. **Kəsim** `s[başla:bit:addım]` alt-sətir qaytarır, bit daxil deyil. Sətirlər **dəyişməzdir**: `s[0] = "x"` yazmaq olmaz, dəyişiklik yeni sətir yaradır.',
      ],
      code: String.raw`s = "Azərbaycan"
print(len(s), s[0], s[-1])        # 10 A n
print(s[0:3], s[3:], s[::-1])     # Azə rbaycan nacyabrəzA
print("bay" in s)                 # True
print("ha" * 3, "Sal" + "am")     # hahaha Salam

path = r"C:\yeni\fayl.txt"        # \y və \f xüsusi məna daşımır
text = """birinci sətir
ikinci sətir"""
print(ord("A"), chr(66))          # 65 B`,
    },
    {
      id: 'set-2', src: '5.2', title: 'Sətirlərin əsas metodları',
      text: [
        'Sətir metodları orijinalı dəyişmir, **yeni sətir qaytarır**: `lower()`, `upper()`, `title()`, `strip()` (kənar boşluqları silir), `replace(a, b)`.',
        'Bölmək və birləşdirmək üçün `split(ayırıcı)` siyahı qaytarır, `", ".join(siyahı)` isə siyahını sətirə çevirir. Axtarış üçün `find()` (tapılmasa `-1`), `count()`, `startswith()`, `endswith()`. Yoxlamalar: `isdigit()`, `isalpha()`, `isspace()`.',
      ],
      code: String.raw`line = "  Python çox rahatdır  "
clean = line.strip()
print(clean.upper(), clean.replace("çox", "həqiqətən"))

words = clean.split()                 # ["Python", "çox", "rahatdır"]
print(len(words), "-".join(words))    # 3 Python-çox-rahatdır

print("12345".isdigit(), "abc1".isalpha())     # True False
print("kitab".find("t"), "kitab".count("a"))   # 2 1
print("fayl.csv".endswith(".csv"))             # True`,
    },
    {
      id: 'set-3', src: '5.3', title: 'Sətirlərin formatlaşdırılması',
      text: [
        'Ən rahat üsul **f-sətirlərdir**: sətrin qarşısına `f` qoyub `{ifadə}` daxilinə istənilən ifadəni yazırsan. İki nöqtədən sonra format göstərilir: `.2f` (2 rəqəm kəsr), `05d` (5 xanaya sıfırlarla), `>10` (sağa düzləmə), `,` (min ayırıcısı).',
        '`str.format()` və köhnə `%` üsulu da işləyir, amma yeni kodda f-sətir üstünlük təşkil edir. `{x=}` yazılışı dəyişənin adını və dəyərini birlikdə çap edir, bu da sazlamada köməkdir.',
      ],
      code: String.raw`pi = 3.14159265
count = 42
name = "Leyla"

print(f"π təqribən {pi:.2f}")             # π təqribən 3.14
print(f"№{count:05d}")                    # №00042
print(f"{name:>10}|{name:<10}|{name:^10}|")
print(f"{1234567:,}")                     # 1,234,567
print(f"{count=}")                        # count=42
print("{} və {}".format("a", "b"))        # a və b`,
    },
  ],
});

PY_CHAPTERS.push({
  id: 'kolleksiyalar',
  title: 'Kolleksiyalar',
  blurb: 'Siyahı, kortej, lüğət, çoxluq və onlarla işləmək üsulları.',
  lessons: [
    {
      id: 'kol-1', src: '3.1', title: 'Siyahılar',
      text: [
        'Siyahı (`list`) elementlərin sıralı və **dəyişdirilə bilən** kolleksiyasıdır: `[1, 2, 3]`. İndeks və kəsim sətirlərdəki kimidir. Elementlər müxtəlif tipli ola bilər, siyahının içində siyahı da ola bilər.',
        'Əsas metodlar: `append` (sona əlavə), `extend` (başqa siyahını əlavə), `insert(i, x)`, `remove(x)`, `pop()` (sonuncunu götürüb qaytarır), `sort()` (yerində sıralayır), `reverse()`. `sorted(s)` isə yeni siyahı qaytarır.',
        'Diqqət: `b = a` yeni siyahı yaratmır, eyni siyahıya ikinci ad verir. Surət üçün `a.copy()` və ya `a[:]` yaz.',
      ],
      code: String.raw`nums = [5, 2, 9]
nums.append(1)
nums.extend([7, 3])
nums.insert(0, 100)
print(nums)                  # [100, 5, 2, 9, 1, 7, 3]
last = nums.pop()            # 3
nums.sort()
print(nums, len(nums))       # [1, 2, 5, 7, 9, 100] 6
print(sorted(nums, reverse=True))

a = [1, 2]
b = a            # eyni siyahı
c = a.copy()     # ayrıca surət
b.append(3)
print(a, c)      # [1, 2, 3] [1, 2]`,
      task: 'Siyahıdakı ən böyük ədədi `max()` işlətmədən tap.',
      answer: String.raw`nums = [4, 17, 9, 12]
best = nums[0]
for x in nums:
    if x > best:
        best = x
print(best)`,
    },
    {
      id: 'kol-2', src: '3.2', title: 'Kortejlər',
      text: [
        'Kortej (`tuple`) siyahıya oxşayır, amma **dəyişməzdir**: yaradıldıqdan sonra elementləri dəyişmir. Mötərizə ilə yazılır: `(1, 2)`. Bir elementli kortejdə vergül mütləqdir: `(5,)`.',
        'Kortejlər sabit qruplar üçün yaxşıdır: koordinat, tarix, funksiyadan bir neçə dəyər qaytarmaq. Dəyişməz olduğundan lüğətdə açar kimi də işlənə bilər.',
      ],
      code: String.raw`point = (3, 4)
x, y = point                  # açılış
print(x, y, point[0])

single = (5,)
print(type(single), type((5)))   # tuple int

def min_max(values):
    return min(values), max(values)    # kortej qaytarır

lo, hi = min_max([4, 9, 1])
print(lo, hi)                          # 1 9

places = {(0, 0): "mərkəz", (1, 0): "sağ"}
print(places[(1, 0)])`,
    },
    {
      id: 'kol-3', src: '3.5', title: 'Diapazonlar (range)',
      text: [
        '`range` ədədlər ardıcıllığıdır və **tənbəl** işləyir: bütün ədədləri yaddaşda saxlamır, lazım olanda hesablayır. Üç forma: `range(dayan)`, `range(başla, dayan)`, `range(başla, dayan, addım)`. Son ədəd daxil deyil.',
        'Geriyə saymaq üçün addım mənfi olur. Siyahı lazımdırsa `list(range(...))` yaz. `len()`, indeks və `in` yoxlaması `range` üzərində də işləyir.',
      ],
      code: String.raw`print(list(range(5)))             # [0, 1, 2, 3, 4]
print(list(range(2, 10, 3)))      # [2, 5, 8]
print(list(range(10, 0, -2)))     # [10, 8, 6, 4, 2]

r = range(1, 1000001)
print(len(r), 500000 in r)        # 1000000 True, yaddaş az tutur`,
    },
    {
      id: 'kol-4', src: '3.3', title: 'Lüğətlər',
      text: [
        'Lüğət (`dict`) **açar: dəyər** cütləri saxlayır: `{"ad": "Leyla", "yaş": 15}`. Açar dəyişməz tipdə olmalıdır (sətir, ədəd, kortej). Python 3.7-dən etibarən lüğət əlavə olunma sırasını saxlayır.',
        '`d[açar]` olmayan açarda `KeyError` verir, `d.get(açar, susmaya_görə)` isə xəta vermir. Lüğət üzərində `keys()`, `values()`, `items()` ilə dövr edə, `pop()` və `del` ilə silə bilərsən. `in` açarın olub-olmadığını yoxlayır.',
      ],
      code: String.raw`student = {"ad": "Leyla", "yaş": 15}
student["sinif"] = "9A"                 # əlavə
student["yaş"] = 16                     # dəyişmə
print(student.get("ünvan", "yoxdur"))   # yoxdur

for key, value in student.items():
    print(key, "->", value)

print("ad" in student, len(student))    # True 3
del student["sinif"]

grades = {}
for word in "a b a c b a".split():
    grades[word] = grades.get(word, 0) + 1
print(grades)                           # {'a': 3, 'b': 2, 'c': 1}`,
      task: 'Mətndəki hər sözün neçə dəfə işləndiyini lüğətlə say.',
      answer: String.raw`text = "bir iki bir üç iki bir"
counts = {}
for word in text.split():
    counts[word] = counts.get(word, 0) + 1
print(counts)       # {'bir': 3, 'iki': 2, 'üç': 1}`,
    },
    {
      id: 'kol-5', src: '3.4', title: 'Çoxluqlar',
      text: [
        'Çoxluq (`set`) **təkrarsız** elementlərin sırasız kolleksiyasıdır. Təkrarlananları silmək və elementin olub-olmadığını tez yoxlamaq üçün idealdır. Boş çoxluğu `set()` ilə yarat, çünki `{}` boş lüğətdir.',
        'Riyazi əməllər: birləşmə `|`, kəsişmə `&`, fərq `-`, simmetrik fərq `^`. `add`, `remove` (olmasa xəta), `discard` (xəta vermir) metodları da var.',
      ],
      code: String.raw`a = {1, 2, 3, 3, 2}
print(a)                            # {1, 2, 3}

x = {1, 2, 3, 4}
y = {3, 4, 5}
print(x | y, x & y)                 # {1, 2, 3, 4, 5} {3, 4}
print(x - y, x ^ y)                 # {1, 2} {1, 2, 5}

nums = [3, 1, 3, 2, 1]
print(sorted(set(nums)))            # [1, 2, 3]  təkrarsız və sıralı

seen = set()
seen.add("a")
seen.discard("zzz")                 # xəta vermir
print("a" in seen)`,
    },
    {
      id: 'kol-6', src: '3.6', title: 'List comprehension',
      text: [
        '«Siyahı generatoru» (list comprehension) yeni siyahını bir sətirdə qurmağa imkan verir: `[ifadə for element in kolleksiya if şərt]`. Eyni qaydada çoxluq və lüğət də qurula bilər.',
        'Sadə çevirmə və süzgəc üçün çox oxunaqlıdır. Məntiq mürəkkəbləşəndə (bir neçə iç-içə dövr, uzun şərt) adi `for` dövrü daha anlaşıqlı olur.',
      ],
      code: String.raw`squares = [x * x for x in range(6)]
print(squares)                          # [0, 1, 4, 9, 16, 25]

evens = [x for x in range(10) if x % 2 == 0]
print(evens)                            # [0, 2, 4, 6, 8]

names = ["leyla", "kamran", "aysel"]
print([n.title() for n in names])       # ['Leyla', 'Kamran', 'Aysel']

lengths = {n: len(n) for n in names}    # lüğət
print(lengths)
pairs = [(i, j) for i in range(2) for j in range(2)]
print(pairs)                            # [(0, 0), (0, 1), (1, 0), (1, 1)]`,
      task: '1-dən 20-yə qədər ədədlərin içindən 3-ə bölünənlərin kvadratlarından siyahı qur.',
      answer: String.raw`result = [x * x for x in range(1, 21) if x % 3 == 0]
print(result)`,
    },
    {
      id: 'kol-7', src: '3.7', title: 'Qablaşdırma və açılış',
      text: [
        '**Açılış** (unpacking) kolleksiyanın elementlərini bir neçə dəyişənə bir əmrlə bölür: `a, b = (1, 2)`. Dəyişənlərin dəyərini dəyişmək üçün də ən qısa yoldur: `a, b = b, a`.',
        'Ulduzlu dəyişən qalan elementləri siyahı kimi yığır: `first, *rest = siyahı`. Lazımsız dəyər üçün ənənəvi olaraq `_` adı yazılır. İç-içə strukturlar da açıla bilər.',
      ],
      code: String.raw`a, b = 1, 2
a, b = b, a
print(a, b)                      # 2 1

first, *rest = [10, 20, 30, 40]
print(first, rest)               # 10 [20, 30, 40]

*head, last = "salam"
print(head, last)                # ['s', 'a', 'l', 'a'] m

name, (x, y) = "nöqtə", (3, 4)   # iç-içə
_, year, _ = "12.05.2010".split(".")
print(name, x, y, year)`,
    },
    {
      id: 'kol-8', src: '3.9', title: 'zip funksiyası',
      text: [
        '`zip(a, b, ...)` kolleksiyaları «fermuar» kimi birləşdirir və hər addımda elementlərdən kortej qaytarır. Ən qısa kolleksiya bitəndə dayanır. Python 3.10-dan `strict=True` uzunluqlar fərqli olanda xəta verir.',
        'İki siyahını paralel gəzmək və ya açar və dəyər siyahılarından lüğət qurmaq üçün istifadə olunur. `zip(*cütlər)` isə əks əməli (ayırmanı) edir.',
      ],
      code: String.raw`names = ["Leyla", "Kamran", "Aysel"]
scores = [90, 75, 88]

for n, s in zip(names, scores):
    print(n, s)

print(dict(zip(names, scores)))          # {'Leyla': 90, 'Kamran': 75, 'Aysel': 88}

pairs = [(1, "a"), (2, "b"), (3, "c")]
nums, letters = zip(*pairs)              # ayırma
print(nums, letters)                     # (1, 2, 3) ('a', 'b', 'c')

for i, name in enumerate(names, start=1):    # nömrələmə üçün enumerate
    print(i, name)`,
    },
  ],
});
