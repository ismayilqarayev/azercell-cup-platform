/* Python bölməsinin dərsləri, 4-cü hissə: fayllarla iş və standart kitabxana (modullar, paketlər, tarix, vaxt). */
PY_CHAPTERS.push({
  id: 'fayllar',
  title: 'Fayllarla iş',
  blurb: 'Mətn, CSV və ikili fayllar, qovluqlar, arxivlər.',
  lessons: [
    {
      id: 'fayl-1', src: '4.1', title: 'Faylları açmaq və bağlamaq',
      text: [
        '`open(yol, rejim, encoding=...)` faylı açır. Rejimlər: `"r"` oxu (susmaya görə), `"w"` yaz (köhnə məzmunu silir), `"a"` sona əlavə, `"x"` yalnız yoxdursa yarat. İkili fayl üçün rejimə `b` əlavə olunur (`"rb"`, `"wb"`).',
        'Faylı həmişə `with` ilə aç: blokdan çıxanda fayl, xəta olsa belə, avtomatik bağlanır. Azərbaycan hərfləri üçün kodlaşdırmanı açıq göstər: `encoding="utf-8"`. Olmayan faylı oxumaq `FileNotFoundError` verir.',
      ],
      code: String.raw`with open("qeydler.txt", "w", encoding="utf-8") as f:
    f.write("Birinci sətir\n")
    f.write("İkinci sətir\n")

try:
    with open("yoxdur.txt", encoding="utf-8") as f:
        print(f.read())
except FileNotFoundError:
    print("Fayl tapılmadı")`,
    },
    {
      id: 'fayl-2', src: '4.2', title: 'Mətn faylları',
      text: [
        'Oxuma üsulları: `read()` bütün məzmunu bir sətir kimi, `readline()` bir sətir, `readlines()` sətirlərin siyahısını qaytarır. Böyük fayllar üçün ən yaxşısı faylın özü üzərində dövr etməkdir: sətir-sətir oxunur və yaddaş az tutulur.',
        'Yazarkən `write()` sətir sonu əlavə etmir, `\\n`-i özün yazmalısan. Çoxlu sətir üçün `writelines()` var. Sətir sonundakı `\\n`-i `strip()` ilə təmizləmək adətdir.',
      ],
      code: String.raw`lines = ["alma", "armud", "nar"]
with open("meyveler.txt", "w", encoding="utf-8") as f:
    for item in lines:
        f.write(item + "\n")

with open("meyveler.txt", encoding="utf-8") as f:
    for number, line in enumerate(f, start=1):
        print(number, line.strip())

with open("meyveler.txt", "a", encoding="utf-8") as f:
    f.write("heyva\n")`,
      task: 'Fayldakı sətirlərin sayını və ən uzun sətri tap.',
      answer: String.raw`with open("meyveler.txt", encoding="utf-8") as f:
    lines = [line.strip() for line in f]
print(len(lines), max(lines, key=len))`,
    },
    {
      id: 'fayl-3', src: '4.3', title: 'CSV faylları',
      text: [
        'CSV (vergüllə ayrılmış dəyərlər) cədvəl məlumatının sadə formatıdır. `csv` modulu sətirləri düzgün bölür (dırnaqlı sahələr, ayırıcı simvolu daxilində vergül). `csv.reader` hər sətri siyahı, `csv.DictReader` isə başlıq sətrinə görə lüğət kimi verir.',
        'Faylı `newline=""` ilə aç, yoxsa Windows-da yazarkən artıq boş sətirlər yarana bilər. Ayırıcı vergül deyilsə `delimiter=";"` göstər.',
      ],
      code: String.raw`import csv

rows = [
    {"ad": "Leyla", "bal": 90},
    {"ad": "Kamran", "bal": 75},
]
with open("netice.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=["ad", "bal"])
    writer.writeheader()
    writer.writerows(rows)

with open("netice.csv", newline="", encoding="utf-8") as f:
    for row in csv.DictReader(f):
        print(row["ad"], int(row["bal"]))`,
    },
    {
      id: 'fayl-4', src: '4.4', title: 'İkili (binary) fayllar',
      text: [
        'Şəkil, səs, arxiv kimi mətn olmayan fayllar `"rb"` və `"wb"` rejimində açılır və `str` yox, **`bytes`** ilə işləyir. Fayl böyükdürsə hamısını yaddaşa yükləməmək üçün hissə-hissə oxu.',
        'Python obyektlərini ikili formada saxlamaq üçün `pickle` modulu var, amma etibarsız mənbədən gələn `pickle` faylını açma: o, kod icra edə bilər.',
      ],
      code: String.raw`# faylı hissə-hissə kopyalamaq
with open("sekil.png", "rb") as src, open("sekil_kopya.png", "wb") as dst:
    while True:
        chunk = src.read(64 * 1024)
        if not chunk:
            break
        dst.write(chunk)

data = bytes([72, 105, 33])
print(data, data.decode("ascii"))      # b'Hi!' Hi!
print("ə".encode("utf-8"))             # b'\xc9\x99'`,
    },
    {
      id: 'fayl-5', src: '4.6', title: 'shelve modulu',
      text: [
        '`shelve` lüğətə bənzər, amma məlumatı fayla yazan saxlama yeridir: açar sətirdir, dəyər demək olar ki, istənilən Python obyekti. Kiçik layihələrdə tam verilənlər bazası qurmadan vəziyyəti proqramlar arasında saxlamaq üçün rahatdır.',
        'Daxildə `pickle` işlədiyindən yalnız özünün yazdığı fayla etibar et. Mürəkkəb sorğular və eyni vaxtda çoxlu istifadəçi üçün `sqlite3` kimi bazalar daha uyğundur.',
      ],
      code: String.raw`import shelve

with shelve.open("oyun") as db:
    db["rekord"] = 1250
    db["oyunçular"] = ["Leyla", "Kamran"]

with shelve.open("oyun") as db:
    print(db["rekord"], list(db.keys()))
    db["rekord"] = max(db["rekord"], 1400)`,
    },
    {
      id: 'fayl-6', src: '4.5', title: 'os modulu və fayl sistemi ilə iş',
      text: [
        '`os` və `os.path` cari qovluğu öyrənmək, qovluğun məzmununu oxumaq, yol qurmaq, fayl və qovluq yaratmaq, silmək və adını dəyişmək üçündür. Yolları əl ilə `+` ilə birləşdirmə, `os.path.join` işlət ki, Windows və Linux-da düzgün işləsin.',
        'Müasir alternativ `pathlib.Path` sinfidir: yol obyekt kimi olur (`Path("a") / "b.txt"`). Hər iki üsul standart kitabxanadadır.',
      ],
      code: String.raw`import os
from pathlib import Path

print(os.getcwd())
os.makedirs("hesabat/2026", exist_ok=True)
path = os.path.join("hesabat", "2026", "notlar.txt")
open(path, "w", encoding="utf-8").close()

print(os.path.exists(path), os.path.isfile(path))
print(os.listdir("hesabat"))

for p in Path("hesabat").rglob("*.txt"):    # pathlib ilə
    print(p, p.stat().st_size)

os.rename(path, os.path.join("hesabat", "2026", "yeni.txt"))`,
    },
    {
      id: 'fayl-7', src: '5.4', title: 'Söz sayma proqramı',
      text: [
        'Əvvəlki dərslərin birləşməsi: faylı oxu, mətni sözlərə böl, hərf böyüklüyünü və durğu işarələrini nəzərə al, hər sözün sayını tap, ən çox işlənənləri çap et. `collections.Counter` bu sayğaclıq işini bir sətirdə görür.',
      ],
      code: String.raw`import re
from collections import Counter

with open("kitab.txt", encoding="utf-8") as f:
    text = f.read().lower()

words = re.findall(r"\w+", text)          # hərf-rəqəm ardıcıllıqları
counts = Counter(words)

print("Cəmi söz:", len(words), "unikal:", len(counts))
for word, n in counts.most_common(5):
    print(f"{word:<12}{n}")`,
    },
    {
      id: 'fayl-8', src: '4.7', title: 'zip arxivlərinin yazılması və oxunması',
      text: [
        '`zipfile` modulu ZIP arxivi yaradır, məzmununu göstərir və açır. `ZipFile(ad, "w")` ilə fayl əlavə et (`write`), `"r"` rejimində `namelist()` ilə siyahıya bax, `extractall(qovluq)` ilə hamısını aç. `ZIP_DEFLATED` sıxma tətbiq edir.',
      ],
      code: String.raw`import zipfile

with zipfile.ZipFile("arxiv.zip", "w", zipfile.ZIP_DEFLATED) as z:
    z.write("meyveler.txt")
    z.write("netice.csv")

with zipfile.ZipFile("arxiv.zip") as z:
    print(z.namelist())
    z.extractall("acilmis")`,
    },
  ],
});

PY_CHAPTERS.push({
  id: 'kitabxana',
  title: 'Modullar, paketlər və standart kitabxana',
  blurb: 'Kodu fayllara bölmək, paket quraşdırmaq, hazır modullar, tarix və vaxt, sazlama.',
  lessons: [
    {
      id: 'kit-1', src: '2.10', title: 'Modulların təyin olunması və qoşulması',
      text: [
        'Hər `.py` faylı moduldur. Başqa fayldakı kodu `import modul`, `from modul import ad` və ya `import modul as qısa` ilə qoşursan. Python modulu cari qovluqda, sonra quraşdırılmış paketlərdə axtarır.',
        'Faylı həm modul, həm də proqram kimi işlətmək üçün işə salma kodunu `if __name__ == "__main__":` blokuna yaz: fayl birbaşa işlədiləndə bu blok icra olunur, import olunanda yox.',
      ],
      code: String.raw`# geometriya.py
import math

def circle_area(r):
    return math.pi * r ** 2

if __name__ == "__main__":
    print(circle_area(2))      # yalnız birbaşa işlədəndə çap olunur

# main.py
from geometriya import circle_area
import geometriya as geo
print(circle_area(1), geo.circle_area(3))`,
    },
    {
      id: 'kit-2', src: '6.6', lang: 'shell', title: 'Modulların bayt-kodunun yaradılması',
      text: [
        'Modulu ilk dəfə import edəndə Python onu bayt-koda tərcümə edib `__pycache__` qovluğuna `.pyc` faylı kimi yazır. Növbəti dəfə mənbə dəyişməyibsə hazır fayl yüklənir, bu da **yüklənməni** sürətləndirir (proqramın özünün işləmə sürətini yox).',
        '`.pyc` fayllarını versiya idarəetməsinə (git) əlavə etmə. Əl ilə tərcümə etmək üçün `python -m compileall`, yazılmasını söndürmək üçün `-B` seçimi var.',
      ],
      code: String.raw`python -m compileall .       # qovluqdakı bütün .py faylları tərcümə et
python -B main.py            # .pyc faylı yazma
echo "__pycache__/" >> .gitignore`,
    },
    {
      id: 'kit-3', src: '6.8', lang: 'shell', title: 'Paketlərin quraşdırılması, pip və virtual mühit',
      text: [
        '`pip` Python paketlərini PyPI deposundan quraşdırır. Hər layihəyə ayrıca **virtual mühit** yaratmaq paketlərin versiyalarının bir-birinə qarışmasının qarşısını alır: mühit `.venv` qovluğunda öz Python-u və paketləri ilə yaşayır.',
        'Layihənin asılılıqlarını `requirements.txt` faylında saxla ki, başqası eyni mühiti bir əmrlə qursun.',
      ],
      code: String.raw`python -m venv .venv
.venv\Scripts\activate          # Windows
source .venv/bin/activate       # macOS / Linux

pip install requests
pip freeze > requirements.txt
pip install -r requirements.txt
deactivate`,
    },
    {
      id: 'kit-4', src: '6.10', lang: 'shell', title: 'uv paket meneceri',
      text: [
        '`uv` Astral şirkətinin Rust ilə yazdığı çox sürətli Python paket və layihə meneceridir. Bir alət virtual mühiti, asılılıqları və Python versiyasını idarə edir, `pip` və `venv` işini daha tez görür.',
        'Layihənin asılılıqları `pyproject.toml` faylında saxlanılır, dəqiq versiyalar isə kilid faylındadır. Beləliklə nəticə hər kompüterdə eyni olur.',
      ],
      code: String.raw`uv init layihem            # yeni layihə qovluğu
cd layihem
uv add requests            # asılılıq əlavə et
uv run main.py             # mühitdə işlət
uv sync                    # kilid faylına görə mühiti qur`,
    },
    {
      id: 'kit-5', src: '6.1', title: 'random modulu',
      text: [
        '`random` psevdo-təsadüfi ədədlər verir: `random()` 0 ilə 1 arası kəsr, `randint(a, b)` hər iki ucu daxil tam ədəd, `uniform(a, b)` kəsr, `choice(siyahı)` təsadüfi element, `sample(siyahı, k)` təkrarsız `k` element, `shuffle(siyahı)` siyahını yerində qarışdırır.',
        '`seed(ədəd)` ardıcıllığı sabitləyir: eyni seed həmişə eyni nəticə verir, test və təkrarlanan təcrübələr üçün faydalıdır.',
      ],
      code: String.raw`import random

random.seed(42)                        # təkrarlanan nəticə
print(random.randint(1, 6))            # zər
print(random.choice(["daş", "kağız", "qayçı"]))

cards = list(range(1, 11))
random.shuffle(cards)
print(cards[:3], random.sample(cards, 2))
print(round(random.uniform(1.0, 2.0), 3))`,
    },
    {
      id: 'kit-6', src: '6.9', title: 'secrets modulu',
      text: [
        'Parol, token və açar kimi təhlükəsizlik tələb edən dəyərlər üçün `random` yararsızdır, çünki ardıcıllığı proqnozlaşdırıla bilər. `secrets` modulu əməliyyat sisteminin kriptoqrafik mənbəyindən istifadə edir.',
      ],
      code: String.raw`import secrets
import string

print(secrets.token_hex(16))          # 32 simvollu hex token
print(secrets.token_urlsafe(16))      # URL-də istifadə üçün
print(secrets.randbelow(100))         # 0..99

alphabet = string.ascii_letters + string.digits
password = "".join(secrets.choice(alphabet) for _ in range(12))
print(password)`,
    },
    {
      id: 'kit-7', src: '6.2', title: 'Riyazi funksiyalar və math modulu',
      text: [
        'Daxili funksiyalar: `abs`, `round`, `pow`, `min`, `max`, `sum`. Qalanı `math` modulundadır: `sqrt`, `floor`, `ceil`, `log`, `sin`, `cos`, `factorial`, `gcd`, `isqrt`, `comb` və `pi`, `e` sabitləri.',
        '`math.floor` aşağı, `math.ceil` yuxarı yuvarlaqlaşdırır. Tam ədədin kvadrat kökünün tam hissəsi üçün `isqrt` dəqiqdir və kəsr xətası yaratmır.',
      ],
      code: String.raw`import math

print(math.sqrt(144), math.isqrt(50))      # 12.0 7
print(math.floor(-2.5), math.ceil(-2.5))   # -3 -2
print(math.factorial(6), math.gcd(12, 18)) # 720 6
print(math.comb(5, 2))                     # 10
print(round(math.pi, 4), math.log(math.e), math.log2(1024))
print(math.dist((0, 0), (3, 4)))           # 5.0`,
    },
    {
      id: 'kit-8', src: '6.4', title: 'decimal modulu',
      text: [
        '`float` ikilik sistemdə təqribi saxlandığından `0.1 + 0.2` tam `0.3` olmur. Pul və dəqiq onluq hesabları üçün `decimal.Decimal` istifadə et. Ədədi **sətirdən** yarat (`Decimal("0.1")`), yoxsa kəsr xətası da daxil olur.',
        '`quantize` nəticəni istənilən onluq mövqeyə yuvarlaqlaşdırır, `getcontext().prec` isə dəqiqliyi təyin edir.',
      ],
      code: String.raw`from decimal import Decimal, ROUND_HALF_UP, getcontext

print(0.1 + 0.2 == 0.3)                        # False
print(Decimal("0.1") + Decimal("0.2"))          # 0.3

price = Decimal("19.99")
total = price * 3
print(total.quantize(Decimal("0.1"), rounding=ROUND_HALF_UP))   # 60.0

getcontext().prec = 30
print(Decimal(1) / Decimal(7))`,
    },
    {
      id: 'kit-9', src: '6.3', title: 'locale modulu',
      text: [
        '`locale` ədəd, pul və tarixin yerli qaydalarla (min ayırıcısı, onluq vergül, valyuta) göstərilməsi üçündür. Mövcud lokalların adları əməliyyat sistemindən asılıdır, ona görə konkret ad (`"az_AZ"`) hər kompüterdə olmaya bilər, həmişə `try/except` ilə qoru.',
        'Boş sətir `""` istifadəçinin sistem lokalını seçir.',
      ],
      code: String.raw`import locale

try:
    locale.setlocale(locale.LC_ALL, "")        # sistem lokalı
except locale.Error:
    print("Lokal tapılmadı, standart istifadə olunur")

print(locale.format_string("%.2f", 1234567.891, grouping=True))
print(locale.getlocale())`,
    },
    {
      id: 'kit-10', src: '8.1', title: 'datetime modulu',
      text: [
        '`datetime` modulunun əsas siniflər: `date` (tarix), `time` (vaxt), `datetime` (hər ikisi). `datetime.now()` indiki anı, `date.today()` bu günü verir. Elementlərə `year`, `month`, `day`, `hour` və s. atributları ilə çıxırsan.',
        'Tarixi sətirə `strftime(format)`, sətri tarixə `datetime.strptime(mətn, format)` çevirir. Tez-tez işlənən işarələr: `%d` gün, `%m` ay, `%Y` il, `%H` saat, `%M` dəqiqə, `%S` saniyə.',
      ],
      code: String.raw`from datetime import datetime, date

now = datetime.now()
print(now.year, now.month, now.day)
print(now.strftime("%d.%m.%Y %H:%M"))

d = datetime.strptime("15.03.2026 14:30", "%d.%m.%Y %H:%M")
print(d, d.hour)

print(date.today(), date(2026, 5, 28).isoformat())    # 2026-05-28`,
    },
    {
      id: 'kit-11', src: '8.2', title: 'Tarixlər üzərində əməliyyatlar',
      text: [
        'İki tarixin fərqi `timedelta` obyektidir (`days`, `seconds`). Tarixə `timedelta` əlavə edib çıxmaqla gələcək və keçmiş tarixi tapırsan. Tarixlər `<`, `>`, `==` ilə müqayisə olunur.',
        '`weekday()` həftənin gününü verir (0 bazar ertəsi, 6 bazar), `replace()` tarixin bir hissəsini dəyişir, `fromisoformat()` ISO formatlı sətri oxuyur.',
      ],
      code: String.raw`from datetime import date, datetime, timedelta

start = date(2026, 9, 15)
exam = date(2026, 12, 1)
print((exam - start).days, "gün qalıb")           # 77 gün qalıb

print(start + timedelta(days=30))                  # 2026-10-15
print(start.weekday(), start < exam)               # 1 True

moment = datetime.fromisoformat("2026-05-28T09:30:00")
print(moment + timedelta(hours=5, minutes=15))
print(start.replace(day=1))                        # 2026-09-01`,
    },
    {
      id: 'kit-12', src: '6.7', title: 'timeit modulu',
      text: [
        'Kodun nə qədər sürətli işlədiyini ölçmək üçün `timeit` modulu kodu çox dəfə təkrarlayıb orta vaxtı verir, tək ölçmənin təsadüfi dəyişmələrindən qaçırsan. `timeit.timeit(kod, number=...)` ümumi saniyəni qaytarır.',
        'Eyni nəticəni verən iki həlli müqayisə etmək üçün əladır. Komanda sətrində `python -m timeit "kod"` yazılışı da işləyir.',
      ],
      code: String.raw`import timeit

a = timeit.timeit("sum([x * x for x in range(1000)])", number=2000)
b = timeit.timeit("sum(x * x for x in range(1000))", number=2000)
print(f"siyahı: {a:.3f} san, generator: {b:.3f} san")

# komanda sətrində:
# python -m timeit "'-'.join(str(n) for n in range(100))"`,
    },
    {
      id: 'kit-13', src: '6.11', title: 'pdb modulu və proqramın sazlanması',
      text: [
        '`pdb` Python-un daxili debaqeridir: proqramı istədiyin yerdə dayandırıb dəyişənlərə baxmağa və addım-addım gəzməyə imkan verir. Kodda dayanacaq qoymaq üçün `breakpoint()` yaz, və ya proqramı `python -m pdb fayl.py` ilə işə sal.',
        'Əsas əmrlər: `n` (növbəti sətir), `s` (funksiyaya gir), `c` (davam et), `p ifadə` (dəyəri çap et), `l` (kodu göstər), `q` (çıx).',
      ],
      code: String.raw`def average(nums):
    total = 0
    for n in nums:
        total += n
    breakpoint()            # burada dayanır, p total, n ilə dəyərlərə bax
    return total / len(nums)

print(average([4, 8, 6]))`,
    },
    {
      id: 'kit-14', src: '6.12', title: 'sched modulu: cədvəl üzrə əməllər',
      text: [
        '`sched.scheduler` əməliyyatları gələcək vaxt üçün planlaşdırır: `enter(gecikmə, prioritet, funksiya, argumentlər)` planlaşdırır, `run()` isə növbəni icra edir və bütün əməliyyatlar bitənə qədər **bloklayır**. `cancel()` planlaşdırılanı ləğv edir.',
        'Sadə təkrarlanan işlər üçün kifayətdir. Mürəkkəb cədvəllər (cron kimi) üçün üçüncü tərəf kitabxanaları və ya sistemin cron/Task Scheduler alətləri istifadə olunur.',
      ],
      code: String.raw`import sched
import time

s = sched.scheduler(time.time, time.sleep)

def say(text):
    print(time.strftime("%H:%M:%S"), text)

s.enter(1, 1, say, ("1 saniyədən sonra",))
s.enter(3, 1, say, ("3 saniyədən sonra",))
event = s.enter(10, 1, say, ("bu işləməyəcək",))
s.cancel(event)

s.run()`,
    },
  ],
});
