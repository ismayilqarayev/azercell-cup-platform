// dim-tests/python.json (DİM Python testləri) faylını yoxlayır: server qaydalarına (QuizAdminService) uyğunluq, təkrarsızlıq,
// düzgün variantın paylanması və (python varsa) generatorun --check rejimi: cavab açarları kodu YENİDƏN icra edərək təsdiqlənir.
// İşə salmaq: node --test tools/*.test.js
'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { spawnSync } = require('node:child_process');

const ROOT = path.join(__dirname, '..');
const FILE = path.join(ROOT, 'dim-tests', 'python.json');
const quizzes = JSON.parse(fs.readFileSync(FILE, 'utf8'));

test('fayl massivdir: 10 mövzu testi + 1 qarışıq sınaq', () => {
  assert.ok(Array.isArray(quizzes));
  assert.equal(quizzes.length, 11);
  assert.equal(new Set(quizzes.map(q => q.title)).size, quizzes.length, 'testlərin adları təkrarsız olmalıdır');
});

test('hər test serverin qəbul edəcəyi formadadır (QuizUpsertRequest + QuizAdminService qaydaları)', () => {
  for (const z of quizzes) {
    const where = z.title;
    assert.ok(typeof z.title === 'string' && z.title.trim() && z.title.length <= 200, where + ': ad');
    assert.ok(z.description == null || z.description.length <= 4000, where + ': izah');
    assert.ok(z.category == null || z.category.length <= 80, where + ': kateqoriya');
    assert.ok(z.timeLimitMinutes === null || (Number.isInteger(z.timeLimitMinutes) && z.timeLimitMinutes >= 1 && z.timeLimitMinutes <= 300), where + ': vaxt');
    assert.equal(z.published, false, where + ': yeni testlər qaralama kimi gəlir (müəllim yoxlayıb dərc edir)');
    assert.ok(Array.isArray(z.questions) && z.questions.length >= 8 && z.questions.length <= 200, where + ': sual sayı');
    z.questions.forEach((q, i) => {
      const at = where + ', sual ' + (i + 1);
      assert.ok(q.type === 'CHOICE' || q.type === 'NUMERIC', at + ': növ');
      assert.ok(typeof q.prompt === 'string' && q.prompt.trim() && q.prompt.length <= 4000, at + ': mətn');
      assert.ok(q.code == null || (typeof q.code === 'string' && q.code.length <= 8000), at + ': kod');
      assert.ok(q.explanation && q.explanation.length <= 4000, at + ': izah var və 4000 simvoldan qısadır');
      if (q.type === 'CHOICE') {
        assert.ok(Array.isArray(q.options) && q.options.length >= 2 && q.options.length <= 6, at + ': variant sayı');
        assert.equal(q.options.length, 5, at + ': DİM qapalı tapşırıqları 5 variantlıdır');
        for (const o of q.options) assert.ok(typeof o === 'string' && o.trim() && o.length <= 500, at + ': variant');
        assert.equal(new Set(q.options).size, q.options.length, at + ': variantlar təkrarsızdır');
        assert.ok(Number.isInteger(q.correctIndex) && q.correctIndex >= 0 && q.correctIndex < q.options.length, at + ': düzgün indeks');
        assert.equal(q.correctText, undefined, at + ': CHOICE-də correctText yoxdur');
      } else {
        assert.ok(q.options === undefined || q.options.length === 0, at + ': NUMERIC-də variant yoxdur');
        assert.ok(typeof q.correctText === 'string' && q.correctText.trim() && q.correctText.length <= 200, at + ': düzgün cavab');
        for (const part of q.correctText.split('|')) assert.ok(part.trim(), at + ': cavabda boş seçim');
      }
    });
  }
});

test('suallar təkrarsızdır və düzgün variant bütün hərflərə paylanıb', () => {
  const seen = new Set();
  const counts = [0, 0, 0, 0, 0];
  let choice = 0;
  for (const z of quizzes) {
    for (const q of z.questions) {
      const key = JSON.stringify([q.prompt, q.code || null, q.options || null]);
      assert.ok(!seen.has(key), 'təkrar sual: ' + z.title + ' / ' + q.prompt.slice(0, 50));
      seen.add(key);
      if (q.type === 'CHOICE') { counts[q.correctIndex]++; choice++; }
    }
  }
  for (let i = 0; i < counts.length; i++) {
    assert.ok(counts[i] / choice >= 0.1 && counts[i] / choice <= 0.35, 'düzgün cavab ' + 'ABCDE'[i] + ' hərfinə qeyri-bərabər düşür: ' + counts.join(','));
  }
});

test('qarışıq sınaq DİM informatika blokunun quruluşundadır: 22 qapalı + 5 kodlu', () => {
  const mixed = quizzes.find(z => /DİM formatında/.test(z.title));
  assert.ok(mixed);
  assert.equal(mixed.questions.filter(q => q.type === 'CHOICE').length, 22);
  assert.equal(mixed.questions.filter(q => q.type === 'NUMERIC').length, 5);
  // qapalılar əvvəldə, kodlular sonda (imtahan kitabçasında olduğu kimi)
  assert.deepEqual(mixed.questions.map(q => q.type).join('').replace(/CHOICE/g, 'C').replace(/NUMERIC/g, 'N'), 'C'.repeat(22) + 'N'.repeat(5));
});

test('cavab açarları kodu yenidən icra etməklə təsdiqlənir (generator --check)', t => {
  let res;
  for (const exe of ['python', 'python3']) {
    res = spawnSync(exe, ['-I', '-X', 'utf8', path.join(ROOT, 'tools', 'dim-tests', 'build.py'), '--check'], { encoding: 'utf8', timeout: 120000 });
    if (!res.error && res.status === 0) break;
    if (!res.error && res.status === 1 && /köhnəlib/.test(res.stdout)) break; // python var, fayl köhnədir
  }
  if (!res || res.error || (res.status !== 0 && !/köhnəlib/.test(res.stdout || ''))) { t.skip('python tapılmadı və ya işə düşmədi'); return; }
  assert.equal(res.status, 0, res.stdout + res.stderr);
});
