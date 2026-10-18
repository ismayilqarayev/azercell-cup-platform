// frontend-src/python-lessons/*.js dərslərinin quruluşunu və kod nümunələrinin sintaksisini yoxlayır.
// İşə salmaq: node --test tools/*.test.js   (kod nümunələrinin yoxlanması üçün sistemdə python lazımdır, yoxdursa o test keçilir)
'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { spawnSync } = require('node:child_process');

const DIR = path.join(__dirname, '..', 'frontend-src', 'python-lessons');
const context = { PY_CHAPTERS: [] };
vm.createContext(context);
for (const f of fs.readdirSync(DIR).filter(n => n.endsWith('.js')).sort()) {
  vm.runInContext(fs.readFileSync(path.join(DIR, f), 'utf8'), context, { filename: f });
}
const chapters = context.PY_CHAPTERS;
const lessons = chapters.flatMap(c => c.lessons);

// metanit.com/python/tutorial/ mövzu siyahısındakı bütün dərslərin nömrələri (85); hər biri dəqiq bir dəfə olmalıdır
const EXPECTED_SOURCES = [
  '1.1', '1.2', '1.3', '1.4', '1.5', '1.6', '1.7',
  '2.1', '2.2', '2.3', '2.4', '2.5', '2.6', '2.7', '2.8', '2.9', '2.10', '2.11', '2.12', '2.13', '2.14', '2.15', '2.16', '2.17', '2.18',
  '2.19', '2.20', '2.21', '2.22', '2.23', '2.24', '2.25', '2.26', '2.27', '2.28',
  '3.1', '3.2', '3.3', '3.4', '3.5', '3.6', '3.7', '3.8', '3.9',
  '4.1', '4.2', '4.3', '4.4', '4.5', '4.6', '4.7',
  '5.1', '5.2', '5.3', '5.4',
  '6.1', '6.2', '6.3', '6.4', '6.5', '6.6', '6.7', '6.8', '6.9', '6.10', '6.11', '6.12',
  '7.1', '7.2', '7.3', '7.4', '7.5', '7.6', '7.7', '7.8',
  '8.1', '8.2',
  '10.1', '10.2', '10.3', '10.4', '10.5', '10.6', '10.7', '10.8',
];

test('11 fəsil və 85 dərs var, id-lər təkrarsızdır', () => {
  assert.equal(chapters.length, 11);
  assert.equal(lessons.length, 85);
  assert.equal(new Set(lessons.map(l => l.id)).size, lessons.length);
  assert.equal(new Set(chapters.map(c => c.id)).size, chapters.length);
  for (const c of chapters) assert.ok(c.title && c.blurb && c.lessons.length > 0, 'fəsil məlumatı: ' + c.id);
});

test('hər Metanit dərsi dəqiq bir dəfə örtülür (keçidlər düzgündür)', () => {
  const got = lessons.map(l => l.src).sort((a, b) => parseFloat(a) - parseFloat(b) || a.localeCompare(b, undefined, { numeric: true }));
  const want = EXPECTED_SOURCES.slice().sort((a, b) => parseFloat(a) - parseFloat(b) || a.localeCompare(b, undefined, { numeric: true }));
  assert.deepEqual(got, want);
  for (const l of lessons) assert.match(l.src, /^\d+\.\d+$/, l.id);
});

test('hər dərsdə başlıq, izah, kod var; tapşırığın cavabı var', () => {
  for (const l of lessons) {
    assert.ok(l.title && l.title.length > 3, 'başlıq: ' + l.id);
    assert.ok(Array.isArray(l.text) && l.text.length >= 1, 'izah: ' + l.id);
    for (const p of l.text) assert.ok(p.length > 20 && p.length <= 900, 'abzas uzunluğu: ' + l.id + ' (' + p.length + ')');
    assert.ok(l.code && l.code.trim().length > 10, 'kod: ' + l.id);
    assert.ok(l.lang === undefined || l.lang === 'shell', 'lang: ' + l.id);
    if (l.task) assert.ok(l.answer && l.answer.trim(), 'tapşırığın cavabı: ' + l.id);
    if (l.answer) assert.ok(l.task, 'cavabın tapşırığı: ' + l.id);
  }
});

test('mətn işarələri tarazdır (` və **), HTML teqi yoxdur', () => {
  for (const l of lessons) {
    for (const t of [...l.text, l.tip || '', l.task || '']) {
      assert.equal((t.match(/`/g) || []).length % 2, 0, 'tək sayda ` : ' + l.id);
      // kod daxilindəki ** (qüvvət operatoru) qalın mətn işarəsi sayılmır
      assert.equal((t.replace(/`[^`]*`/g, '').match(/\*\*/g) || []).length % 2, 0, 'tək sayda ** : ' + l.id);
      // kod parçası daxilində < > göstərilə bilər (məs. <Sinif object ...>): səhifə mətni HTML kimi yox, qaçırılmış şəkildə yazır
      assert.ok(!/<\/?[a-z][^>]*>/i.test(t.replace(/`[^`]*`/g, '')), 'HTML teqi: ' + l.id);
    }
  }
});

test('Python kod nümunələri sintaksis baxımından düzgündür', t => {
  const snippets = [];
  for (const l of lessons) {
    if (l.lang !== 'shell') snippets.push({ id: l.id, code: l.code });
    if (l.answer) snippets.push({ id: l.id + ' (cavab)', code: l.answer });
  }
  const script = 'import sys, json, ast\nbad = []\nfor it in json.load(sys.stdin):\n    try:\n        ast.parse(it["code"])\n    except SyntaxError as e:\n        bad.append(it["id"] + ": " + str(e))\nprint(json.dumps(bad))\n';
  let res;
  for (const exe of ['python', 'python3']) {
    res = spawnSync(exe, ['-X', 'utf8', '-I', '-c', script], { input: JSON.stringify(snippets), encoding: 'utf8', timeout: 30000 });
    if (!res.error && res.status === 0) break;
  }
  if (!res || res.error || res.status !== 0) { t.skip('python tapılmadı və ya işə düşmədi'); return; }
  assert.deepEqual(JSON.parse(res.stdout), []);
});
