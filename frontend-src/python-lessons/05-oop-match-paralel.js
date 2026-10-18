/* Python bölməsinin dərsləri, 5-ci hissə: obyekt yönümlü proqramlaşdırma, pattern matching, paralellik və asinxronluq. */
PY_CHAPTERS.push({
  id: 'oop',
  title: 'Obyekt yönümlü proqramlaşdırma',
  blurb: 'Siniflər, miras, property, operator yükləmə, abstrakt siniflər və dataclass.',
  lessons: [
    {
      id: 'oop-1', src: '7.1', title: 'Siniflər və obyektlər',
      text: [
        'Sinif obyektlərin layihəsidir: hansı məlumatı (**atributlar**) saxladığını və nə edə bildiyini (**metodlar**) təsvir edir. Sinifdən yaradılan hər obyekt (nümunə) öz atributlarına malikdir.',
        '`__init__` obyekt yaradılanda avtomatik çağırılır və başlanğıc dəyərləri təyin edir. Metodun birinci parametri `self` həmin obyektin özüdür: onun vasitəsilə atributlara çıxırsan. Sinif adı `PascalCase` ilə yazılır.',
      ],
      code: String.raw`class Student:
    def __init__(self, name, grade):
        self.name = name
        self.grade = grade
        self.scores = []

    def add_score(self, score):
        self.scores.append(score)

    def average(self):
        return sum(self.scores) / len(self.scores) if self.scores else 0

leyla = Student("Leyla", 9)
leyla.add_score(90)
leyla.add_score(84)
print(leyla.name, leyla.average())     # Leyla 87.0

kamran = Student("Kamran", 10)         # ayrıca obyekt, öz atributları var
print(kamran.scores)                   # []`,
      task: 'Balansı olan `BankAccount` sinfi yaz: `deposit(x)` və `withdraw(x)` metodları, balans mənfi olmasın.',
      answer: String.raw`class BankAccount:
    def __init__(self, balance=0):
        self.balance = balance

    def deposit(self, amount):
        self.balance += amount

    def withdraw(self, amount):
        if amount > self.balance:
            raise ValueError("Balans kifayət etmir")
        self.balance -= amount

acc = BankAccount(100)
acc.withdraw(30)
print(acc.balance)   # 70`,
    },
    {
      id: 'oop-2', src: '7.2', title: 'İnkapsulyasiya, atributlar və property',
      text: [
        'İnkapsulyasiya məlumatı və onunla işləyən kodu bir yerdə saxlamaq, daxili detalları gizlətməkdir. Python-da gizlətmə **razılaşma** ilə aparılır: `_ad` «daxili istifadə üçündür» deməkdir, `__ad` isə adı dəyişdirərək (name mangling) kənardan təsadüfi çıxışı çətinləşdirir.',
        '`@property` metodu atribut kimi oxunur və dəyər yoxlaması əlavə etməyə imkan verir. `@ad.setter` yazma zamanı yoxlama aparır. Beləliklə atribut sonradan hesablanan olsa da çağıran kod dəyişmir.',
      ],
      code: String.raw`class Temperature:
    def __init__(self, celsius):
        self.celsius = celsius          # setter işləyir

    @property
    def celsius(self):
        return self._celsius

    @celsius.setter
    def celsius(self, value):
        if value < -273.15:
            raise ValueError("Mütləq sıfırdan aşağı ola bilməz")
        self._celsius = value

    @property
    def fahrenheit(self):               # hesablanan atribut
        return self._celsius * 9 / 5 + 32

t = Temperature(25)
print(t.celsius, t.fahrenheit)          # 25 77.0
t.celsius = 30
# t.celsius = -500   ->  ValueError`,
    },
    {
      id: 'oop-3', src: '7.3', title: 'Miras',
      text: [
        'Miras yeni sinfin mövcud sinfin atributlarını və metodlarını götürməsidir: `class Uşaq(Valideyn):`. Ortaq məntiq baza sinifdə, fərqli hissə törəmə sinifdə yazılır. Baza sinfin `__init__`-ini `super().__init__(...)` ilə çağırırsan.',
        '`isinstance(obj, Sinif)` obyektin sinifini (və ya onun törəməsini), `issubclass` isə siniflər arasındakı əlaqəni yoxlayır. Python-da bir neçə baza sinifdən də miras almaq olur, metodun axtarış sırası `Sinif.__mro__`-da görünür.',
      ],
      code: String.raw`class Person:
    def __init__(self, name):
        self.name = name

    def introduce(self):
        return f"Mən {self.name}"

class Teacher(Person):
    def __init__(self, name, subject):
        super().__init__(name)
        self.subject = subject

t = Teacher("Aydın", "informatika")
print(t.introduce(), t.subject)
print(isinstance(t, Person), issubclass(Teacher, Person))   # True True`,
    },
    {
      id: 'oop-4', src: '7.4', title: 'Baza sinfin funksionallığının əvəzlənməsi',
      text: [
        'Törəmə sinif baza sinifdəki metodu eyni adla yenidən yazaraq **əvəz edə** bilər (override). Lazım olsa köhnə davranışı `super().metod()` ilə çağırıb üzərinə əlavə edirsən.',
        'Bu, **polimorfizmin** əsasıdır: müxtəlif siniflərin obyektləri eyni adlı metodla çağırılır, hər biri öz qaydasında cavab verir. Çağıran kod konkret sinfi bilmək məcburiyyətində deyil.',
      ],
      code: String.raw`class Animal:
    def speak(self):
        return "..."

class Dog(Animal):
    def speak(self):
        return "Hav"

class Cat(Animal):
    def speak(self):
        return "Miyav"

class Puppy(Dog):
    def speak(self):
        return super().speak() + " (zəif səslə)"

for pet in [Dog(), Cat(), Puppy(), Animal()]:
    print(type(pet).__name__, pet.speak())`,
    },
    {
      id: 'oop-5', src: '7.6', title: 'Sinif atributları və statik metodlar',
      text: [
        'Sinfin özünə aid atribut (bütün obyektlər üçün ortaq) metodların xaricində yazılır. `@classmethod` birinci parametr kimi obyekti yox, **sinfi** (`cls`) alır və çox vaxt alternativ konstruktor kimi işlədilir. `@staticmethod` isə nə obyekti, nə sinfi alır, sadəcə sinfə aid köməkçi funksiyadır.',
      ],
      code: String.raw`class Circle:
    count = 0                      # sinif atributu, ortaq sayğac
    PI = 3.14159

    def __init__(self, radius):
        self.radius = radius
        Circle.count += 1

    @classmethod
    def from_diameter(cls, d):     # alternativ konstruktor
        return cls(d / 2)

    @staticmethod
    def is_valid(radius):
        return radius > 0

    def area(self):
        return Circle.PI * self.radius ** 2

a = Circle(2)
b = Circle.from_diameter(10)
print(Circle.count, b.radius, Circle.is_valid(-1))   # 2 5.0 False`,
    },
    {
      id: 'oop-6', src: '7.5', title: 'object sinfi və obyektin sətir təsviri',
      text: [
        'Python-da bütün siniflər gizli şəkildə `object` sinfindən miras alır. Obyekti `print` ilə çap edəndə standart olaraq anlaşılmaz `<Sinif object at 0x...>` görünür. Bunu dəyişmək üçün iki xüsusi metod var: `__str__` (istifadəçi üçün oxunaqlı mətn) və `__repr__` (proqramçı üçün dəqiq təsvir, sazlamada görünür).',
        '`__eq__` iki obyektin bərabərliyini təyin edir. Ona uyğun `__hash__` də yazmasan, obyekt çoxluqda və lüğət açarında işlənə bilməz.',
      ],
      code: String.raw`class Point:
    def __init__(self, x, y):
        self.x, self.y = x, y

    def __repr__(self):
        return f"Point({self.x}, {self.y})"

    def __str__(self):
        return f"({self.x}; {self.y})"

    def __eq__(self, other):
        return isinstance(other, Point) and (self.x, self.y) == (other.x, other.y)

    def __hash__(self):
        return hash((self.x, self.y))

p = Point(1, 2)
print(p)                    # (1; 2)
print([p], repr(p))         # [Point(1, 2)] Point(1, 2)
print(p == Point(1, 2), len({p, Point(1, 2)}))   # True 1`,
    },
    {
      id: 'oop-7', src: '7.7', title: 'Operatorların yüklənməsi',
      text: [
        '`__add__`, `__sub__`, `__mul__` kimi xüsusi metodlar sənin siniflərində `+`, `-`, `*` operatorlarının necə işləyəcəyini təyin edir. Müqayisə üçün `__eq__`, `__lt__`, `__gt__`, ölçü üçün `__len__`, indeks üçün `__getitem__`, `bool()` üçün `__bool__` var.',
        'Operatoru yalnız mənası aydın olanda yüklə (vektor toplanması, pul cəmi). Yoxsa kodu oxumaq çətinləşir.',
      ],
      code: String.raw`class Vector:
    def __init__(self, x, y):
        self.x, self.y = x, y

    def __add__(self, other):
        return Vector(self.x + other.x, self.y + other.y)

    def __mul__(self, k):
        return Vector(self.x * k, self.y * k)

    def __abs__(self):
        return (self.x ** 2 + self.y ** 2) ** 0.5

    def __repr__(self):
        return f"Vector({self.x}, {self.y})"

a, b = Vector(1, 2), Vector(3, 4)
print(a + b, a * 3, abs(b))      # Vector(4, 6) Vector(3, 6) 5.0`,
    },
    {
      id: 'oop-8', src: '7.8', title: 'Abstrakt siniflər və metodlar',
      text: [
        'Abstrakt sinif yalnız baza kimi xidmət edir və **tam təyin olunmayıb**: onun abstrakt metodlarını bütün törəmə siniflər yazmağa borcludur. Bu, bütün siniflərin eyni «müqaviləyə» uyğun gəlməsini təmin edir. `abc.ABC` sinfindən miras alınır, metod `@abstractmethod` ilə işarələnir.',
        'Abstrakt sinifdən obyekt yaratmaq mümkün deyil (`TypeError`), abstrakt metodların hamısını yazmayan törəmə sinifdən də.',
      ],
      code: String.raw`from abc import ABC, abstractmethod

class Shape(ABC):
    @abstractmethod
    def area(self):
        ...

class Rectangle(Shape):
    def __init__(self, w, h):
        self.w, self.h = w, h

    def area(self):
        return self.w * self.h

class Square(Shape):
    pass                           # area() yoxdur

print(Rectangle(3, 4).area())      # 12
# Shape()    ->  TypeError
# Square()   ->  TypeError: abstrakt metod yazılmayıb`,
    },
    {
      id: 'oop-9', src: '6.5', title: 'dataclasses modulu və data-siniflər',
      text: [
        '`@dataclass` dekoratoru yalnız məlumat saxlayan siniflər üçün `__init__`, `__repr__` və `__eq__` metodlarını **avtomatik** yazır. Sahələr tip qeydləri ilə elan olunur, susmaya görə dəyər də verilə bilər.',
        'Dəyişkən susmaya görə dəyər (siyahı) üçün `field(default_factory=list)` yaz. `frozen=True` obyekti dəyişməz edir, `order=True` müqayisəni əlavə edir, `asdict()` obyekti lüğətə çevirir.',
      ],
      code: String.raw`from dataclasses import dataclass, field, asdict

@dataclass(order=True)
class Student:
    name: str
    grade: int = 9
    scores: list = field(default_factory=list)

a = Student("Leyla", 9, [90, 85])
b = Student("Kamran")
print(a)                       # Student(name='Leyla', grade=9, scores=[90, 85])
print(a == Student("Leyla", 9, [90, 85]))     # True
print(asdict(b))
print(sorted([a, b]))          # ad üzrə sıralanır`,
    },
  ],
});

PY_CHAPTERS.push({
  id: 'match',
  title: 'Pattern matching (match konstruksiyası)',
  blurb: 'Python 3.10-dan gələn güclü seçim konstruksiyası: dəyər, kortej, siyahı, lüğət və sinif şablonları.',
  lessons: [
    {
      id: 'match-1', src: '2.13', title: 'match konstruksiyası',
      text: [
        '`match` ifadənin dəyərini ardıcıl `case` şablonları ilə müqayisə edir və ilk uyğun gələn blok icra olunur. Başqa dillərin `switch`-indən fərqli olaraq burada **şablon** yazılır, yalnız bərabərlik yox. Python 3.10 və yuxarı versiyada işləyir.',
        '`|` ilə bir neçə dəyər birləşdirilir, `_` isə «istənilən başqa halı» tutur və adətən sonda yazılır.',
      ],
      code: String.raw`def describe(code):
    match code:
        case 200:
            return "OK"
        case 301 | 302:
            return "yönləndirmə"
        case 404:
            return "tapılmadı"
        case _:
            return "naməlum"

print(describe(302), describe(404), describe(999))`,
    },
    {
      id: 'match-2', src: '2.22', title: 'Kortejlər pattern matching-də',
      text: [
        'Kortej şablonu elementlərin sayına və dəyərinə görə uyğunluğu yoxlayır və uyğun gələndə elementləri dəyişənlərə bağlayır. Sabit dəyər (`0`) yoxlama, ad (`x`) isə bağlama rolunu oynayır.',
      ],
      code: String.raw`def locate(point):
    match point:
        case (0, 0):
            return "mərkəz"
        case (x, 0):
            return f"X oxunda, x={x}"
        case (0, y):
            return f"Y oxunda, y={y}"
        case (x, y):
            return f"nöqtə {x}, {y}"

print(locate((0, 0)), locate((5, 0)), locate((2, 3)))`,
    },
    {
      id: 'match-3', src: '2.23', title: 'Siyahılar (massivlər) pattern matching-də',
      text: [
        'Siyahı şablonu `[a, b]` kimi yazılır və ardıcıllıqların uzunluğuna baxır. `*qalan` ulduzlu ad artıq elementləri tutur, `[]` isə boş siyahıdır. Sətirlər ardıcıllıq sayılsa da `match`-də siyahı şablonu ilə uyğun gəlmir.',
      ],
      code: String.raw`def check(items):
    match items:
        case []:
            return "boşdur"
        case [one]:
            return f"bir element: {one}"
        case [first, second]:
            return f"iki element: {first}, {second}"
        case [first, *others]:
            return f"{first} və daha {len(others)} element"

for data in ([], [7], [1, 2], [5, 6, 7, 8]):
    print(check(data))`,
    },
    {
      id: 'match-4', src: '2.24', title: 'Lüğətlər pattern matching-də',
      text: [
        'Lüğət şablonu `{"açar": şablon}` kimi yazılır və lüğətdə **həmin açarların olub-olmadığını** yoxlayır, əlavə açarlar uyğunluğu pozmur. Dəyər yerinə ad yazsan, dəyər həmin adda dəyişənə bağlanır. Qalan açarları `**rest` ilə götürmək olar.',
      ],
      code: String.raw`def handle(event):
    match event:
        case {"type": "click", "x": x, "y": y}:
            return f"klik ({x}, {y})"
        case {"type": "key", "value": value}:
            return f"düymə {value}"
        case {"type": t}:
            return f"naməlum hadisə: {t}"

print(handle({"type": "click", "x": 3, "y": 8, "button": "sol"}))
print(handle({"type": "key", "value": "A"}))
print(handle({"type": "scroll"}))`,
    },
    {
      id: 'match-5', src: '2.25', title: 'Siniflər pattern matching-də',
      text: [
        'Sinif şablonu `Sinif(atribut=şablon)` formasındadır: əvvəl obyektin sinfini, sonra göstərilən atributları yoxlayır. Mövqeli yazılış (`Point(0, 0)`) üçün sinifdə `__match_args__` olmalıdır, `@dataclass` onu avtomatik yaradır.',
      ],
      code: String.raw`from dataclasses import dataclass

@dataclass
class Point:
    x: int
    y: int

@dataclass
class Circle:
    center: Point
    radius: int

def info(shape):
    match shape:
        case Point(x=0, y=0):
            return "koordinat başlanğıcı"
        case Point(x, y):
            return f"nöqtə ({x}, {y})"
        case Circle(center=Point(0, 0), radius=r):
            return f"mərkəzi başlanğıcda olan çevrə, r={r}"
        case _:
            return "bilinməyən fiqur"

print(info(Point(0, 0)), info(Point(2, 5)), info(Circle(Point(0, 0), 4)))`,
    },
    {
      id: 'match-6', src: '2.26', title: 'Guard (şablona əlavə şərt)',
      text: [
        'Şablon uyğun gələndən sonra `case şablon if şərt:` ilə **əlavə şərt** yoxlaya bilərsən. Şərt `False` olsa, Python növbəti `case`-ə keçir. Beləliklə şablon strukturu, `if` isə dəyəri yoxlayır.',
      ],
      code: String.raw`def sign(n):
    match n:
        case 0:
            return "sıfır"
        case x if x > 0:
            return "müsbət"
        case _:
            return "mənfi"

def triangle(point):
    match point:
        case (x, y) if x == y:
            return "diaqonalda"
        case (x, y) if x > y:
            return "aşağıda"
        case _:
            return "yuxarıda"

print(sign(5), sign(-2), sign(0), triangle((3, 3)))`,
    },
    {
      id: 'match-7', src: '2.27', title: 'Ləqəblərin təyini və AS şablonu',
      text: [
        '`as ad` şablonun uyğun gələn **bütöv** hissəsini dəyişənə bağlayır. Şablon mürəkkəb olanda həm hissələrə, həm də bütövə ehtiyac olarsa faydalıdır. `|` ilə birləşdirilmiş seçimlərdə hansının uyğun gəldiyini də bununla tuturuq.',
      ],
      code: String.raw`def process(data):
    match data:
        case (x, y) as pair:
            return f"cüt {pair}, cəmi {x + y}"
        case 1 | 2 | 3 as small:
            return f"kiçik ədəd {small}"
        case [("a" | "b") as letter, *_]:
            return f"siyahı {letter} ilə başlayır"
        case _:
            return "başqa"

print(process((4, 5)), process(2), process(["b", 1, 2]))`,
    },
  ],
});

PY_CHAPTERS.push({
  id: 'paralel',
  title: 'Paralellik və asinxronluq',
  blurb: 'Axınlar, proseslər və asyncio: eyni anda bir neçə işi necə görmək olar.',
  lessons: [
    {
      id: 'par-1', src: '10.1', title: 'Çoxaxınlılıq. Thread və axınların yaradılması',
      text: [
        'Axın (thread) proqramın daxilində ayrıca icra xəttidir. `threading.Thread(target=funksiya, args=(...))` axını yaradır, `start()` işə salır, `join()` bitməsini gözləyir. Axınlar eyni yaddaşı paylaşır.',
        'CPython-da **GIL** (Global Interpreter Lock) eyni anda yalnız bir axının Python kodunu icra etməsinə icazə verir. Ona görə axınlar **gözləmə ilə bağlı** işlərdə (şəbəkə, fayl, `sleep`) sürətləndirir, amma ağır hesablamada yox.',
      ],
      code: String.raw`import threading
import time

def download(name, seconds):
    print(name, "başladı")
    time.sleep(seconds)            # gözləmə simulyasiyası
    print(name, "bitdi")

threads = [
    threading.Thread(target=download, args=("fayl-1", 2)),
    threading.Thread(target=download, args=("fayl-2", 2)),
]
start = time.perf_counter()
for t in threads:
    t.start()
for t in threads:
    t.join()
print(f"cəmi {time.perf_counter() - start:.1f} san")   # ~2 san (4 yox)`,
    },
    {
      id: 'par-2', src: '10.2', title: 'Axınların sinxronizasiyası və Lock',
      text: [
        'Bir neçə axın eyni dəyişəni eyni anda dəyişəndə **yarış vəziyyəti** (race condition) yaranır və nəticə təsadüfi olur. `threading.Lock` bir anda yalnız bir axının kritik hissəni icra etməsini təmin edir. `with lock:` yazılışı kilidi götürür və blokdan çıxanda avtomatik buraxır.',
        'Kilidlərdən çox istifadə proqramı yavaşladır və **ölü kilidə** (deadlock) səbəb ola bilər. Mümkünsə axınlar arasında yaddaş paylaşmaqdansa `queue.Queue` ilə məlumat ötür.',
      ],
      code: String.raw`import threading

counter = 0
lock = threading.Lock()

def increase(times):
    global counter
    for _ in range(times):
        with lock:                 # kilidsiz nəticə təsadüfi ola bilər
            counter += 1

threads = [threading.Thread(target=increase, args=(100_000,)) for _ in range(4)]
for t in threads:
    t.start()
for t in threads:
    t.join()
print(counter)                     # həmişə 400000`,
    },
    {
      id: 'par-3', src: '10.3', title: 'Çoxprosessorluq. Proseslərin yaradılması və işə salınması',
      text: [
        '`multiprocessing` hər iş üçün **ayrıca proses** açır, hər prosesin öz yaddaşı və öz GIL-i var. Buna görə ağır hesablamanı (CPU-bound) bir neçə nüvədə həqiqətən paralel icra etmək olar.',
        'Windows və macOS-da prosesləri yaradan kod mütləq `if __name__ == "__main__":` blokunda olmalıdır. Bir çox eyni işi paylamaq üçün `Pool.map` rahatdır.',
      ],
      code: String.raw`from multiprocessing import Pool, Process
import os

def heavy(n):
    return sum(i * i for i in range(n))

def report(name):
    print(name, "proses nömrəsi", os.getpid())

if __name__ == "__main__":
    p = Process(target=report, args=("köməkçi",))
    p.start()
    p.join()

    with Pool(4) as pool:
        print(pool.map(heavy, [10**6, 2 * 10**6, 3 * 10**6, 4 * 10**6]))`,
    },
    {
      id: 'par-4', src: '10.4', title: 'Proseslər arasında məlumat mübadiləsi',
      text: [
        'Proseslərin yaddaşı ayrı olduğundan dəyişən birbaşa paylaşılmır. Məlumat ötürmək üçün `multiprocessing.Queue` (növbə, ən sadə yol), `Value` və `Array` (paylaşılan yaddaş) və `Manager` (paylaşılan siyahı və lüğət) var.',
      ],
      code: String.raw`from multiprocessing import Process, Queue

def worker(q, numbers):
    for n in numbers:
        q.put(n * n)
    q.put(None)                       # bitdi siqnalı

if __name__ == "__main__":
    q = Queue()
    p = Process(target=worker, args=(q, [1, 2, 3, 4]))
    p.start()
    while True:
        item = q.get()
        if item is None:
            break
        print("gəldi:", item)
    p.join()`,
    },
    {
      id: 'par-5', src: '10.5', title: 'Pipe kanalları',
      text: [
        '`Pipe()` iki proses arasında birbaşa **iki tərəfli** kanal yaradır: iki uc (`conn1`, `conn2`) qaytarır, bir uca `send()` ilə yazılan digərində `recv()` ilə oxunur. İki proses arasında sadə mübadilə üçün `Queue`-dan sürətlidir.',
      ],
      code: String.raw`from multiprocessing import Process, Pipe

def child(conn):
    request = conn.recv()
    conn.send(request.upper())
    conn.close()

if __name__ == "__main__":
    parent_conn, child_conn = Pipe()
    p = Process(target=child, args=(child_conn,))
    p.start()
    parent_conn.send("salam")
    print(parent_conn.recv())         # SALAM
    p.join()`,
    },
    {
      id: 'par-6', src: '10.6', title: 'Asinxronluq və asyncio paketi',
      text: [
        'Asinxron proqram **bir axında** işləyir, amma gözləmə zamanı başqa işə keçir. `async def` korutin yaradır, `await` isə gözləmə nöqtəsidir: hadisə dövrəsi (event loop) bu anda başqa korutini icra edir. Çoxlu şəbəkə sorğusu kimi gözləmə ilə bağlı işlərdə minlərlə tapşırıq az yaddaşla paralel gedir.',
        '`asyncio.run()` əsas korutini başladır, `asyncio.gather()` bir neçə korutini eyni vaxtda işlədir. Hesablama əməliyyatı `await` olmadan dövrəni bloklayır.',
      ],
      code: String.raw`import asyncio
import time

async def fetch(name, seconds):
    print(name, "başladı")
    await asyncio.sleep(seconds)       # gözləyərkən başqa tapşırıq işləyir
    print(name, "bitdi")
    return name.upper()

async def main():
    start = time.perf_counter()
    results = await asyncio.gather(
        fetch("a", 2), fetch("b", 1), fetch("c", 2),
    )
    print(results, f"{time.perf_counter() - start:.1f} san")   # ~2 san

asyncio.run(main())`,
    },
    {
      id: 'par-7', src: '10.8', title: 'Çoxaxınlılıq, çoxprosessorluq və asinxronluq: müqayisə',
      text: [
        'Seçim işin növündən asılıdır. **Gözləmə ilə bağlı** işlər (şəbəkə, fayl, verilənlər bazası): çox sayda tapşırıq üçün `asyncio`, az saydakı və ya bloklayan kitabxanalar üçün `threading`. **Hesablama ilə bağlı** işlər: `multiprocessing`, çünki GIL axınları məhdudlaşdırır.',
        'Ən rahat yol `concurrent.futures` modulundakı `ThreadPoolExecutor` və `ProcessPoolExecutor` sinifləridir: eyni interfeys, yalnız hovuzu dəyişirsən. Əvvəl işin yavaş yerini ölçmədən paralelliyə keçmə, çünki sadə həll çox vaxt kifayət edir.',
      ],
      code: String.raw`from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
import time

def io_task(n):
    time.sleep(1)                  # gözləmə
    return n

def cpu_task(n):
    return sum(i * i for i in range(n))

if __name__ == "__main__":
    with ThreadPoolExecutor(max_workers=5) as pool:        # I/O üçün
        print(list(pool.map(io_task, range(5))))           # ~1 san

    with ProcessPoolExecutor() as pool:                    # CPU üçün
        print(list(pool.map(cpu_task, [10**6] * 4)))`,
    },
  ],
});
