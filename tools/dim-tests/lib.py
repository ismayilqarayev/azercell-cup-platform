"""DİM İnformatika Python testlərini yaradan kitabxana.

Əsas fikir: hər sualın düzgün cavabı əllə yazılmır, sualın koduna Python-da REAL icra edilərək hesablanır.
Beləliklə cavab açarı səhv ola bilməz, yanlış variantlar isə (a) əllə verilmiş tipik səhvlərdən, (b) kodu bir qədər dəyişib
(məs. range-in sonunu, // əvəzinə /) yenə icra edərək tapılan "şagird səhvlərindən" götürülür və düzgün cavaba bərabər ola bilməz.
İzahda hər sətirdən sonra dəyişənlərin dəyərləri (icra izi) avtomatik göstərilir.
"""
import builtins
import contextlib
import io
import re
import sys
import textwrap

STEP_LIMIT = 2_000_000
# Düzgün variantın A..E arasındakı yeri dövr edir ki, cavab həmişə eyni hərfdə olmasın.
_POSITIONS = [2, 0, 4, 1, 3]
_counter = [0]


class StepLimit(Exception):
    pass


def C(code):
    """Üçqat dırnaqlı kodu sətir əvvəlindəki boşluqlardan təmizləyir."""
    return textwrap.dedent(code).strip('\n')


def _norm_out(text):
    lines = [ln.rstrip() for ln in text.split('\n')]
    return '\n'.join(lines).strip('\n')


def _env(stdin):
    it = iter(stdin)

    def fake_input(prompt=''):
        try:
            return next(it)
        except StopIteration:
            raise EOFError('stdin tükənib')

    b = dict(vars(builtins))
    b['input'] = fake_input
    return {'__name__': '__main__', '__builtins__': b}


def run(code, stdin=(), guard=False):
    """Kodu icra edib ekrana çıxanı qaytarır (hər sətrin sonundakı boşluqlar atılır)."""
    env = _env(stdin)
    out = io.StringIO()
    obj = compile(code, '<q>', 'exec')
    counter = [0]

    def g(frame, event, arg):
        if frame.f_code.co_filename != '<q>':
            return None

        def local(frame, event, arg):
            counter[0] += 1
            if counter[0] > STEP_LIMIT:
                raise StepLimit()
            return local
        return local

    old = sys.gettrace()
    try:
        if guard:
            sys.settrace(g)
        with contextlib.redirect_stdout(out):
            exec(obj, env)
    finally:
        sys.settrace(old)
    return _norm_out(out.getvalue())


def trace(code, stdin=()):
    """Modul səviyyəsində hər sətirdən sonra dəyişən dəyişənləri qaytarır: [(sətir_nömrəsi, {ad: dəyər}), ...]."""
    env = _env(stdin)
    out = io.StringIO()
    obj = compile(code, '<q>', 'exec')
    steps = []
    snapshot = {}
    prev = [None]

    def snap(namespace):
        d = {}
        for k, v in namespace.items():
            if k.startswith('__') or callable(v) or isinstance(v, type(sys)):
                continue
            r = repr(v)
            d[k] = r if len(r) <= 40 else r[:37] + '...'
        return d

    def tracer(frame, event, arg):
        if frame.f_code.co_filename != '<q>' or frame.f_code.co_name != '<module>':
            return None

        def local(frame, event, arg):
            if event in ('line', 'return'):
                cur = snap(frame.f_globals)
                diff = {k: v for k, v in cur.items() if snapshot.get(k) != v}
                if diff and prev[0] is not None:
                    steps.append((prev[0], diff))
                snapshot.clear()
                snapshot.update(cur)
                if event == 'line':
                    prev[0] = frame.f_lineno
            return local
        return local

    old = sys.gettrace()
    try:
        sys.settrace(tracer)
        with contextlib.redirect_stdout(out):
            exec(obj, env)
    finally:
        sys.settrace(old)
    return steps


def trace_text(code, stdin=()):
    steps = trace(code, stdin)
    rows = ['Sətir %d: %s' % (ln, '; '.join('%s = %s' % kv for kv in d.items())) for ln, d in steps]
    if len(rows) > 14:
        rows = rows[:10] + ['...'] + rows[-3:]
    return '\n'.join(rows)


def _key(s):
    # Yeni sətir və sətir əvvəlindəki boşluqlar saxlanılır ("a\nb" ilə "a b", "  *" ilə "*" fərqli variantlardır; naxış sualları üçün vacibdir),
    # yalnız sətir sonlarındakı boşluqlar atılır.
    return '\n'.join(line.rstrip() for line in str(s).split('\n')).strip('\n')


def _neighbours(correct):
    try:
        n = int(correct)
    except ValueError:
        return []
    return [str(n + 1), str(n - 1), str(n + 2), str(n - 2), str(n * 2), str(n + 10)]


def _mutations(code, stdin, mut):
    outs = []
    for old, new in mut:
        if old not in code:
            raise ValueError('mutasiya üçün "%s" kodda tapılmadı' % old)
        try:
            outs.append(run(code.replace(old, new, 1), stdin, guard=True))
        except Exception:
            continue
    return outs


def _pick_wrong(correct, wrong, mutated, extra):
    seen = {_key(correct)}
    chosen = []
    for cand in list(wrong) + mutated + extra:
        k = _key(cand)
        if k and k not in seen:
            seen.add(k)
            chosen.append(cand)
        if len(chosen) == 4:
            break
    if len(chosen) < 4:
        raise ValueError('4 yanlış variant tapılmadı (%d): düzgün cavab %r' % (len(chosen), correct))
    return chosen


def _place(correct, wrong):
    pos = _POSITIONS[_counter[0] % len(_POSITIONS)]
    _counter[0] += 1
    options = list(wrong)
    options.insert(pos, correct)
    return options, pos


def _question(qtype, prompt, code, explanation, **fields):
    q = {'type': qtype, 'prompt': prompt, 'code': code, 'explanation': explanation}
    q.update(fields)
    return q


DEFAULT_OUT = 'Aşağıdakı proqramın icrası nəticəsində ekrana nə çıxarılacaq?'
DEFAULT_NUM = 'Aşağıdakı proqramın ekrana çıxardığı nəticəni yazın.'


def _prompt(q, note):
    return q + (' ' + note if note else '')


def _explain(why, code, stdin, answer):
    parts = []
    if why:
        parts.append(why)
    t = trace_text(code, stdin)
    if t:
        parts.append('İcra izi (hər sətirdən sonra dəyişən dəyərlər):\n' + t)
    parts.append('Ekrana çıxan: ' + answer.replace('\n', ' / '))
    return '\n\n'.join(parts)


def out(code, stdin=(), q=DEFAULT_OUT, note=None, wrong=(), mut=(), why=None):
    """Qapalı sual: kodun çıxışı. Düzgün cavab kodu icra edərək tapılır."""
    code = C(code)
    correct = run(code, stdin)
    wr = _pick_wrong(correct, wrong, _mutations(code, stdin, mut), _neighbours(correct))
    options, pos = _place(correct, wr)
    return _question('CHOICE', _prompt(q, note), code, _explain(why, code, stdin, correct),
                     options=options, correctIndex=pos)


def num(code, stdin=(), q=DEFAULT_NUM, note=None, why=None):
    """Kodlu sual: cavab yazılır. Düzgün cavab kodu icra edərək tapılır; bir sətirli olmalıdır."""
    code = C(code)
    correct = run(code, stdin)
    if '\n' in correct or not correct:
        raise ValueError('kodlu sualın cavabı bir sətir olmalıdır: %r' % correct)
    return _question('NUMERIC', _prompt(q, note), code, _explain(why, code, stdin, correct), correctText=correct)


def pick(q, exprs, pred, setup=None, mode='eval', why=None, label=None):
    """Qapalı sual: variantlar ifadələrdir, düzgün olan YALNIZ BİR ifadə pred şərtini ödəməlidir (hər biri icra edilir).

    mode='eval': pred ifadənin hesablanmış qiymətinə baxır; mode='text': pred ifadənin mətninə baxır (məs. ad düzgündürmü).
    label(e, v, ok) izahdakı sətri qaytarır (verilməyibsə eval rejimində "ifadə -> qiymət (tip)" yazılır)."""
    setup = C(setup) if setup else None
    env = {'__builtins__': builtins}
    if setup:
        exec(setup, env)
    verdicts = []
    for e in exprs:
        v = eval(e, env) if mode == 'eval' else e
        verdicts.append((e, v, bool(pred(v))))
    hits = [i for i, (_, _, ok) in enumerate(verdicts) if ok]
    if len(hits) != 1:
        raise ValueError('"%s": şərti ödəyən %d variant var (1 olmalıdır): %r' % (q, len(hits), verdicts))
    wrong = [e for i, e in enumerate(exprs) if i != hits[0]]
    if len(wrong) != 4:
        raise ValueError('5 variant olmalıdır')
    options, pos = _place(exprs[hits[0]], wrong)
    lines = []
    for e, v, ok in verdicts:
        if label:
            lines.append(label(e, v, ok))
        else:
            lines.append('%s  ->  %r%s' % (e, v, ('  (tip: %s)' % type(v).__name__) if mode == 'eval' else ''))
    explanation = ((why + '\n\n') if why else '') + 'Hər variantın qiyməti:\n' + '\n'.join(lines)
    return _question('CHOICE', q, setup, explanation, options=options, correctIndex=pos)


def inp(code, target, lo, hi, which='max', q=None, domain=None, why=None, check_bound=True):
    """Qapalı sual: çıxış `target` olması üçün daxil edilən ən böyük/kiçik ədəd. Hamısı [lo, hi] aralığında brute-force ilə tapılır."""
    code = C(code)
    target = str(target)
    matches = [n for n in range(lo, hi + 1) if run(code, [str(n)]) == target]
    if not matches:
        raise ValueError('heç bir giriş çıxışı %s etmir' % target)
    correct = max(matches) if which == 'max' else min(matches)
    if check_bound:
        if which == 'max' and correct >= hi - 3:
            raise ValueError('axtarış aralığı kifayət deyil (max=%d, hi=%d)' % (correct, hi))
        if which == 'min' and correct <= lo + 3:
            raise ValueError('axtarış aralığı kifayət deyil (min=%d, lo=%d)' % (correct, lo))
    others = [m for m in matches if m != correct]
    cand = []
    other_edge = (min(matches) if which == 'max' else max(matches))
    if other_edge != correct:
        cand.append(other_edge)
    cand += [correct + 1, correct - 1, correct + 2, correct - 2, correct * 2, correct + 10]
    cand += others[::-1][:3]
    wrong = []
    for c in cand:
        if c != correct and c not in wrong and c > 0:
            wrong.append(c)
        if len(wrong) == 4:
            break
    if len(wrong) < 4:
        raise ValueError('inp: 4 yanlış variant tapılmadı')
    options, pos = _place(str(correct), [str(w) for w in wrong])
    word = 'böyük' if which == 'max' else 'kiçik'
    question = q or ('Klaviaturadan n ədədi daxil edilir%s. Proqramın ekrana %s çıxarması üçün n-in ala biləcəyi ən %s qiymət hansıdır?'
                     % ((' (%s)' % domain) if domain else '', target, word))
    shown = [correct, correct + 1 if which == 'max' else correct - 1]
    probe = '; '.join('n = %d  ->  %s' % (n, run(code, [str(n)]) or '(boş)') for n in shown)
    explanation = ((why + '\n\n') if why else '') + 'Yoxlama: ' + probe + '\nƏn %s uyğun qiymət: %d' % (word, correct)
    return _question('CHOICE', question, code, explanation, options=options, correctIndex=pos)


def quiz(title, description, questions, category='Python', time_limit=None):
    # seedKey build.py-də verilir. published=True: bu fayl QuizSeedLoader-in bazaya yazdığı hazır testlərdir (cavab açarları icra ilə yoxlanıb).
    return {
        'title': title,
        'description': description,
        'category': category,
        'timeLimitMinutes': time_limit,
        'published': True,
        'questions': questions,
    }
