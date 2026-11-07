"""DİM Python testlərini yığıb dim-tests/python.json faylına yazır.

İstifadə:
    python -X utf8 tools/dim-tests/build.py           # faylı yenidən yaradır
    python -X utf8 tools/dim-tests/build.py --check   # fayl cari sual bankı ilə eynidirmi (cavablar yenidən icra ilə yoxlanır)

Hər sualın cavabı kodu icra edərək hesablanır, ona görə --check həm də cavab açarlarının doğruluğunu yoxlayır.
Çıxış: massiv, hər element QuizUpsertRequest formatındadır (saytın "JSON ilə test yüklə" bölməsinə yapışdırılır), test qaralama kimi gəlir.
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

OUT = os.path.normpath(os.path.join(HERE, '..', '..', 'dim-tests', 'python.json'))


def build():
    quizzes = [bank1.test1(), bank1.test2(), bank1.test3(), bank1.test4(), bank1.test5(),
               bank2.test6(), bank2.test7(), bank2.test8(), bank2.test9(), bank2.test10(),
               bank3.mixed()]
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
            print('dim-tests/python.json köhnəlib: python -X utf8 tools/dim-tests/build.py ilə yenidən yaradın')
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
        print('  %-62s %3d  %s' % (z['title'], len(z['questions']), kinds))


if __name__ == '__main__':
    main()
