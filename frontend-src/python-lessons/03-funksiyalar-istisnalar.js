/* Python bölməsinin dərsləri, 3-cü hissə: funksiyalar (generatorlar daxil) və istisnalar. */
PY_CHAPTERS.push({
  id: 'funksiyalar',
  title: 'Funksiyalar',
  blurb: 'Funksiya yaratmaq, parametrlər, görünmə sahəsi, lambda, qapanma, dekorator və generator.',
  lessons: [
    {
      id: 'fn-1', src: '2.8', title: 'Funksiyalar',
      text: [
        'Funksiya adlandırılmış kod blokudur: bir dəfə yazıb dəfələrlə çağırırsan. `def ad(parametrlər):` ilə təyin olunur. Funksiyanın ilk sətrində üç dırnaqla **docstring** (təsvir) yazmaq qəbul olunmuş qaydadır.',
        'Funksiya çağırılanda parametrlərə arqumentlər verilir. Funksiya təyin olunmaqla icra olunmur, yalnız çağırıldıqda işləyir; ona görə təyin çağırışdan əvvəl gəlməlidir.',
      ],
      code: String.raw`def greet(name):
    """Adla salamlayır."""
    print(f"Salam, {name}!")

greet("Leyla")
greet("Kamran")

def is_even(n):
    return n % 2 == 0

print(is_even(10), is_even(7))     # True False`,
      task: 'Ədədin sadə olub-olmadığını yoxlayan `is_prime(n)` funksiyası yaz.',
      answer: String.raw`def is_prime(n):
    if n < 2:
        return False
    i = 2
    while i * i <= n:
        if n % i == 0:
            return False
        i += 1
    return True

print([x for x in range(20) if is_prime(x)])   # [2, 3, 5, 7, 11, 13, 17, 19]`,
    },
    {
      id: 'fn-2', src: '2.15', title: 'Funksiyanın parametrləri',
      text: [
        'Arqumentləri **mövqeyə görə** (`f(1, 2)`) və ya **adla** (`f(b=2, a=1)`) vermək olar. Parametrə **susmaya görə dəyər** təyin etsən, o arqument məcburi olmur: `def f(a, b=10)`.',
        'Təhlükəli tələ: susmaya görə dəyər kimi siyahı və ya lüğət yazma (`def f(x=[])`). Həmin obyekt funksiya təyin olunanda bir dəfə yaranır və bütün çağırışlarda paylaşılır. Əvəzində `None` yaz, funksiya daxilində yeni siyahı yarat.',
      ],
      code: String.raw`def power(base, exp=2):
    return base ** exp

print(power(5))             # 25
print(power(2, 10))         # 1024
print(power(exp=3, base=2)) # 8

def add_item(item, box=None):
    if box is None:         # susmaya görə [] yazmaq səhvdir
        box = []
    box.append(item)
    return box

print(add_item("a"), add_item("b"))   # ['a'] ['b']`,
    },
    {
      id: 'fn-3', src: '3.8', title: 'Parametrlərdə qablaşdırma və açılış (*args, **kwargs)',
      text: [
        '`*args` parametri artıq mövqeli arqumentləri **kortej** kimi, `**kwargs` isə adlı arqumentləri **lüğət** kimi yığır. Beləliklə funksiya istənilən sayda arqument qəbul edə bilər.',
        'Çağırışda `*siyahı` elementləri ayrı arqument kimi, `**lüğət` isə cütləri adlı arqument kimi «açır». Bu, bir funksiyanın arqumentlərini başqasına ötürmək üçün tez-tez işlənir.',
      ],
      code: String.raw`def total(*numbers):
    return sum(numbers)

print(total(1, 2, 3, 4))        # 10

def describe(**info):
    for key, value in info.items():
        print(key, "=", value)

describe(ad="Leyla", yaş=15)

values = [4, 5, 6]
print(total(*values))            # 15, siyahı açıldı

config = {"ad": "Kamran", "yaş": 17}
describe(**config)               # lüğət açıldı`,
    },
    {
      id: 'fn-4', src: '2.16', title: 'return operatoru və nəticənin qaytarılması',
      text: [
        '`return` funksiyanı dərhal bitirir və dəyəri çağıran yerə qaytarır. `return` olmasa funksiya `None` qaytarır. Çap etmək (`print`) ilə qaytarmaq fərqli şeylərdir: qaytarılan dəyəri sonra hesablamada istifadə etmək olar.',
        'Bir neçə dəyər qaytarmaq üçün onları vergüllə yaz, Python kortej qaytarır və çağıran tərəf açıb bölə bilər. Erkən `return` çoxlu iç-içə `if`-lərin qarşısını alır.',
      ],
      code: String.raw`def square(x):
    return x * x

def show(x):
    print(x)             # qaytarmır

print(square(4) + 1)     # 17
r = show(3)
print(r)                 # None

def stats(nums):
    if not nums:
        return None      # erkən çıxış
    return min(nums), max(nums), sum(nums) / len(nums)

lo, hi, avg = stats([2, 8, 5])
print(lo, hi, avg)       # 2 8 5.0`,
    },
    {
      id: 'fn-5', src: '2.9', title: 'Dəyişənlərin görünmə sahəsi',
      text: [
        'Dəyişən yalnız təyin olunduğu sahədə görünür. Python ada **LEGB** sırası ilə baxır: Local (funksiyanın içi), Enclosing (əhatə edən funksiya), Global (modul səviyyəsi), Built-in (daxili adlar).',
        'Funksiya daxilində dəyişənə dəyər mənimsətsən yeni lokal dəyişən yaranır. Qlobal dəyişəni dəyişmək üçün `global ad` yaz. Amma qlobal dəyişənlərdən qaçmaq yaxşıdır: funksiyaya dəyəri parametr kimi ötür, nəticəni `return` et.',
      ],
      code: String.raw`counter = 0

def wrong():
    counter = 100        # yeni lokal dəyişəndir
    return counter

def with_global():
    global counter       # qlobal olanı dəyişir
    counter += 1

wrong()
print(counter)           # 0
with_global()
print(counter)           # 1

def outer():
    x = "xaricdə"
    def inner():
        print(x)         # əhatə edən sahədən oxuyur
    inner()
outer()`,
    },
    {
      id: 'fn-6', src: '2.17', title: 'Funksiya tip, parametr və nəticə kimi',
      text: [
        'Python-da funksiya adi dəyərdir. Onu dəyişənə mənimsətmək, siyahıda və ya lüğətdə saxlamaq, başqa funksiyaya arqument kimi vermək və funksiyadan qaytarmaq olar. Belə funksiyalar **yüksək tərtibli** adlanır.',
        'Daxili `map` (hər elementə tətbiq), `filter` (süzgəc) və `sorted(..., key=funksiya)` bu prinsiplə işləyir.',
      ],
      code: String.raw`def double(x):
    return x * 2

def apply(func, value):
    return func(value)

print(apply(double, 21))                 # 42

operations = {"iki dəfə": double, "kvadrat": lambda x: x * x}
for name, op in operations.items():
    print(name, op(5))

words = ["alma", "nar", "heyva"]
print(sorted(words, key=len))            # uzunluğa görə
print(list(map(str.upper, words)))
print(list(filter(lambda w: len(w) > 3, words)))`,
    },
    {
      id: 'fn-7', src: '2.18', title: 'Lambda ifadələri',
      text: [
        '`lambda parametrlər: ifadə` adsız qısa funksiya yaradır. Gövdəsi **tək ifadədir**, nəticəsi avtomatik qaytarılır. Əsasən `sorted`, `max`, `min`, `map`, `filter` kimi funksiyalara qısa açar verəndə işlədilir.',
        'Mürəkkəb məntiq üçün adi `def` istifadə et. Ada bağlanmış lambda (`f = lambda x: ...`) əvəzinə `def` yazmaq daha yaxşı üslubdur.',
      ],
      code: String.raw`square = lambda x: x * x
print(square(7))                              # 49

students = [("Leyla", 90), ("Kamran", 75), ("Aysel", 88)]
students.sort(key=lambda s: s[1], reverse=True)
print(students)                               # bal üzrə azalan

print(max(students, key=lambda s: s[1]))      # ('Leyla', 90)
print(list(map(lambda x: x + 1, [1, 2, 3])))  # [2, 3, 4]`,
    },
    {
      id: 'fn-8', src: '2.19', title: 'Qapanmalar (closure)',
      text: [
        'Daxili funksiya əhatə edən funksiyanın dəyişənlərini «xatırlayırsa» və xarici funksiya artıq bitsə də onlara müraciət edə bilirsə, buna **qapanma** deyilir. Beləliklə vəziyyəti qlobal dəyişən və ya sinif olmadan saxlamaq olur.',
        'Xarici dəyişəni dəyişmək lazım olanda daxili funksiyada `nonlocal` yazılır. Tələ: dövrdə yaradılan lambda-lar dəyişəni **sonda** götürür (`late binding`); həll yolu dəyəri susmaya görə parametr kimi bağlamaqdır.',
      ],
      code: String.raw`def make_counter():
    count = 0
    def next_value():
        nonlocal count
        count += 1
        return count
    return next_value

a = make_counter()
b = make_counter()
print(a(), a(), a(), b())      # 1 2 3 1

def multiplier(k):
    return lambda x: x * k

triple = multiplier(3)
print(triple(14))               # 42

funcs = [lambda x, i=i: x + i for i in range(3)]   # i=i ilə bağlanır
print([f(10) for f in funcs])   # [10, 11, 12]`,
    },
    {
      id: 'fn-9', src: '2.28', title: 'Dekoratorlar',
      text: [
        'Dekorator başqa funksiyanı qəbul edib onun davranışını **dəyişdirmədən** genişləndirən funksiyadır (vaxt ölçmək, log yazmaq, giriş yoxlamaq). `@dekorator` yazılışı `f = dekorator(f)` ilə eynidir.',
        'Daxili `wrapper` funksiyası `*args, **kwargs` qəbul edir ki, istənilən funksiyaya uyğun gəlsin. `functools.wraps` orijinalın adını və docstring-ini qoruyur.',
      ],
      code: String.raw`import time
from functools import wraps

def timed(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        print(f"{func.__name__} {elapsed:.4f} san çəkdi")
        return result
    return wrapper

@timed
def slow_sum(n):
    return sum(range(n))

print(slow_sum(2_000_000))`,
    },
    {
      id: 'fn-10', src: '10.7', title: 'Generatorlar',
      text: [
        'Generator `yield` ilə dəyər «verən» funksiyadır. Hər `yield`-də icra dayanır və növbəti sorğuda qaldığı yerdən davam edir. Dəyərlər bir-bir və lazım olanda hesablanır, ona görə milyonlarla elementi yaddaşda saxlamaq lazım gəlmir.',
        'Generatoru `for` ilə gəzmək və ya `next()` ilə addım-addım almaq olar. Generator ifadəsi `(x * x for x in ...)` siyahı generatoruna bənzəyir, amma mötərizədədir və tənbəl işləyir.',
      ],
      code: String.raw`def fibonacci(limit):
    a, b = 0, 1
    while a < limit:
        yield a
        a, b = b, a + b

print(list(fibonacci(50)))      # [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

gen = fibonacci(10)
print(next(gen), next(gen), next(gen))   # 0 1 1

total = sum(x * x for x in range(1_000_000))   # siyahı yaradılmır
print(total)

def countdown(n):
    while n > 0:
        yield n
        n -= 1
print(list(countdown(3)))       # [3, 2, 1]`,
    },
  ],
});

PY_CHAPTERS.push({
  id: 'istisnalar',
  title: 'İstisnalar və xətaların idarəsi',
  blurb: 'Proqramı xəta zamanı çökməyə qoymamaq: try, except, finally və öz istisnaların.',
  lessons: [
    {
      id: 'ist-1', src: '2.11', title: 'try...except...finally konstruksiyası',
      text: [
        'Xəta baş verəndə Python **istisna** (exception) yaradır və tutulmasa proqram dayanır. `try` blokuna riskli kodu yaz, `except` blokunda xətanı idarə et. `else` yalnız xəta olmayanda, `finally` isə hər halda (məsələn faylı bağlamaq üçün) işləyir.',
        'Əsas qayda: yalnız **gözlədiyin** xətanı tut və əməliyyatı mümkün qədər kiçik `try` blokunda saxla.',
      ],
      code: String.raw`try:
    n = int(input("Ədəd: "))
    result = 100 / n
except ValueError:
    print("Bu ədəd deyil")
except ZeroDivisionError:
    print("Sıfıra bölmək olmaz")
else:
    print("Nəticə:", result)
finally:
    print("Yoxlama bitdi")`,
      task: 'İstifadəçi düzgün tam ədəd daxil edənə qədər soruşan dövr yaz.',
      answer: String.raw`while True:
    try:
        n = int(input("Tam ədəd: "))
        break
    except ValueError:
        print("Yenidən cəhd et")
print("Qəbul olundu:", n)`,
    },
    {
      id: 'ist-2', src: '2.20', title: 'except və müxtəlif istisna tiplərinin idarəsi',
      text: [
        'Bir neçə `except` blokunu yuxarıdan aşağı yoxlanılır və **ilk uyğun gələn** işləyir. Ona görə dəqiq tipləri əvvəl, ümumiləri (`Exception`) axırda yaz. Bir blokda bir neçə tip kortejlə verilə bilər: `except (KeyError, IndexError):`.',
        '`as e` ilə istisna obyektini götürüb mesajını çap edə bilərsən. Boş `except:` yazma, çünki `Ctrl+C` kimi sistem siqnallarını da udur.',
      ],
      code: String.raw`data = {"a": 1}
items = [10, 20]

def read(key, index):
    try:
        return data[key] / items[index]
    except KeyError as e:
        print("Açar yoxdur:", e)
    except (IndexError, ZeroDivisionError) as e:
        print("Əməliyyat mümkün deyil:", type(e).__name__)
    except Exception as e:
        print("Gözlənilməz xəta:", e)

read("b", 0)       # Açar yoxdur: 'b'
read("a", 5)       # Əməliyyat mümkün deyil: IndexError
print(read("a", 1))`,
    },
    {
      id: 'ist-3', src: '2.21', title: 'İstisna yaratmaq və öz istisna tiplərin',
      text: [
        '`raise ValueError("mesaj")` ilə istisnanı özün yarada bilərsən, məsələn gələn məlumat düzgün deyilsə. `Exception`-dan miras alaraq öz istisna sinfini yaradırsan, beləliklə kodu çağıran tərəf konkret xətanı ayrıca tuta bilir.',
        '`except` blokunda sadəcə `raise` yazmaq istisnanı yuxarı ötürür. `raise Yeni() from e` isə səbəb zəncirini saxlayır.',
      ],
      code: String.raw`class NegativeAmountError(Exception):
    """Məbləğ mənfi olduqda."""

def withdraw(balance, amount):
    if amount < 0:
        raise NegativeAmountError(f"Mənfi məbləğ: {amount}")
    if amount > balance:
        raise ValueError("Balans kifayət etmir")
    return balance - amount

try:
    print(withdraw(100, 30))        # 70
    print(withdraw(100, -5))
except NegativeAmountError as e:
    print("Öz istisnamız:", e)
except ValueError as e:
    print("Dəyər xətası:", e)`,
    },
  ],
});
