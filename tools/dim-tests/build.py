"""DİM Python testlərini yığıb backend/src/main/resources/seed/dim-python-tests.json faylına yazır.

İstifadə:
    python -X utf8 tools/dim-tests/build.py           # faylı yenidən yaradır
    python -X utf8 tools/dim-tests/build.py --check   # fayl cari sual bankı ilə eynidirmi (cavablar yenidən icra ilə yoxlanır)

Hər sualın cavabı kodu icra edərək hesablanır, ona görə --check həm də cavab açarlarının doğruluğunu yoxlayır.
Fayl tətbiq başlayanda QuizSeedLoader tərəfindən bazaya yazılır (hər testin sabit seedKey-i var: bir dəfə yaranır, sonra toxunulmur).
Çıxış: massiv, hər element QuizUpsertRequest formatındadır + seedKey (saytın "JSON ilə test yüklə" bölməsi də onu qəbul edir).
"""
import json
import os
import sys

sys.dont_write_bytecode = True  # __pycache__ yaranmasın (repoya düşməsin)

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

import bank1  # noqa: E402
import bank2  # noqa: E402
import bank3  # noqa: E402

OUT = os.path.normpath(os.path.join(HERE, '..', '..', 'backend', 'src', 'main', 'resources', 'seed', 'dim-python-tests.json'))

# Açarlar SABİT qalmalıdır: dəyişsə, yükləyici testi yeni sayıb təkrar yaradar. Testin sırası dəyişsə də açar eyni qalır.
SEED_KEYS = ['dim-python-01', 'dim-python-02', 'dim-python-03', 'dim-python-04', 'dim-python-05',
             'dim-python-06', 'dim-python-07', 'dim-python-08', 'dim-python-09', 'dim-python-10',
             'dim-python-mock']


def build():
    quizzes = [bank1.test1(), bank1.test2(), bank1.test3(), bank1.test4(), bank1.test5(),
               bank2.test6(), bank2.test7(), bank2.test8(), bank2.test9(), bank2.test10(),
               bank3.mixed()]
    assert len(quizzes) == len(SEED_KEYS)
    quizzes = [dict([('seedKey', key)] + list(z.items())) for key, z in zip(SEED_KEYS, quizzes)]
    seen = set()
    for z in quizzes:
        for i, q in enumerate(z['questions'], 1):
            key = (q['prompt'], q.get('code'), tuple(q.get('options') or ()))
            if key in seen:
                raise SystemExit('təkrar sual: %s, sual %d' % (z['title'], i))
            seen.add(key)
    return quizzes


def dump(quizzes):
    return json.dumps(quizzes, ensure_ascii=False, indent=2) + '\n'


def main():
    quizzes = build()
    text = dump(quizzes)
    total = sum(len(z['questions']) for z in quizzes)
    if '--check' in sys.argv:
        with open(OUT, encoding='utf-8', newline='') as f:
            current = f.read()
        if current.replace('\r\n', '\n') != text:
            print('dim-python-tests.json köhnəlib: python -X utf8 tools/dim-tests/build.py ilə yenidən yaradın')
            raise SystemExit(1)
        print('ok: %d test, %d sual, fayl cari sual bankı ilə eynidir' % (len(quizzes), total))
        return
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w', encoding='utf-8', newline='\n') as f:
        f.write(text)
    print('yazıldı: %s (%d test, %d sual)' % (OUT, len(quizzes), total))
    for z in quizzes:
        kinds = {}
        for q in z['questions']:
            kinds[q['type']] = kinds.get(q['type'], 0) + 1
        print('  %-16s %-58s %3d  %s' % (z['seedKey'], z['title'], len(z['questions']), kinds))


if __name__ == '__main__':
    main()
