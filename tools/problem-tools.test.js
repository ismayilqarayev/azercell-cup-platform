// frontend-src/problem-tools.js-in sırf məntiq funksiyalarının testləri. İşə salmaq: node --test tools/
'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const T = require('../frontend-src/problem-tools.js');

// "abc|def" kimi kursorlu mətni (| kursor, |...| seçim üçün iki ədəd) və açar üçün nəticəni tətbiq edir.
function press(marked, key, mods) {
  const first = marked.indexOf('|');
  const second = marked.indexOf('|', first + 1);
  let value, start, end;
  if (second === -1) { value = marked.replace('|', ''); start = end = first; }
  else { value = marked.slice(0, first) + marked.slice(first + 1, second) + marked.slice(second + 1); start = first; end = second - 1; }
  const e = T.editForKey(value, start, end, key, mods);
  if (!e) return null;
  const out = value.slice(0, e.start) + e.text + value.slice(e.end);
  const s = e.selStart, f = e.selEnd;
  return s === f ? out.slice(0, s) + '|' + out.slice(s) : out.slice(0, s) + '|' + out.slice(s, f) + '|' + out.slice(f);
}

test('Tab: boşluqlar növbəti 4-lük dayanacağa qədər', () => {
  assert.equal(press('|x', 'Tab'), '    |x');
  assert.equal(press('ab|c', 'Tab'), 'ab  |c');
  assert.equal(press('abcd|', 'Tab'), 'abcd    |');
});

test('Tab: çox sətirli seçimdə hər sətir girintilənir, boş sətirlərə toxunulmur', () => {
  assert.equal(press('|a\n\nb|', 'Tab'), '    |a\n\n    b|');
});

test('Shift+Tab: girinti geri alınır (4 boşluğa qədər)', () => {
  assert.equal(press('    x|', 'Tab', { shift: true }), 'x|');
  assert.equal(press('  x|', 'Tab', { shift: true }), 'x|');
  assert.equal(press('|    a\n        b|', 'Tab', { shift: true }), '|a\n    b|');
  assert.equal(press('x|', 'Tab', { shift: true }), 'x|'); // girintisi olmayan sətir dəyişmir
});

test('Enter: əvvəlki sətrin girintisi qorunur, { sonrası 4 boşluq artır', () => {
  assert.equal(press('  a|', 'Enter'), '  a\n  |');
  assert.equal(press('int main(){|', 'Enter'), 'int main(){\n    |');
  assert.equal(press('    if (x) {|', 'Enter'), '    if (x) {\n        |');
});

test('Enter: {|} arasında blok açılır', () => {
  assert.equal(press('int main(){|}', 'Enter'), 'int main(){\n    |\n}');
  assert.equal(press('  for(;;){|}', 'Enter'), '  for(;;){\n      |\n  }');
});

test('mötərizə cütləmə və üstündən keçmə', () => {
  assert.equal(press('f|', '('), 'f(|)'); // sözün sonunda "(" cütlənir
  assert.equal(press('f(|)', ')'), 'f()|');
  assert.equal(press('|', '{'), '{|}');
  assert.equal(press('a|b', '('), null); // sözün ortasında cütləmə yoxdur
  assert.equal(press('|abc', '('), null); // hərf ərəfəsində cütləmə yoxdur
});

test('seçim mötərizə ilə əhatə olunur', () => {
  assert.equal(press('|abc|', '('), '(|abc|)');
  assert.equal(press('x |abc| y', '"'), 'x "|abc|" y');
});

test('dırnaq: hərfin yanında cütləmə yoxdur, boşluqdan sonra var, qapanana keçir', () => {
  assert.equal(press('don|', "'"), null);
  assert.equal(press('1|', "'"), null);
  assert.equal(press('x = |', '"'), 'x = "|"');
  assert.equal(press('x = "ab|"', '"'), 'x = "ab"|');
});

test('Backspace boş cütü birlikdə silir', () => {
  assert.equal(press('(|)', 'Backspace'), '|');
  assert.equal(press('"|"', 'Backspace'), '|');
  assert.equal(press('(a|)', 'Backspace'), null);
});

test('} yeni sətirdə girintidən 4 boşluq geri çəkilir', () => {
  assert.equal(press('{\n    |', '}'), '{\n}|');
  assert.equal(press('a|', '}'), null);
});

test('rəngləmə: açar söz, tip, rəqəm, sətir, şərh', () => {
  const h = T.highlightCppLines('int x = 5; // hi')[0];
  assert.match(h, /<span class="pt-tp">int<\/span>/);
  assert.match(h, /<span class="pt-num">5<\/span>/);
  assert.match(h, /<span class="pt-cmt">\/\/ hi<\/span>/);
  assert.match(T.highlightCppLines('for(;;)')[0], /<span class="pt-kw">for<\/span>/);
  assert.ok(T.highlightCppLines('cout << "a<b";')[0].includes('<span class="pt-str">"a&lt;b"</span>'));
  assert.ok(!T.highlightCppLines('int x;')[0].includes('pt-str'));
});

test('rəngləmə: HTML qaçırılır', () => {
  const h = T.highlightCppLines('a < b && c > d')[0];
  assert.ok(h.includes('&lt;') && h.includes('&gt;') && h.includes('&amp;&amp;'));
  assert.ok(!/<\s*b/.test(h.replace(/<span[^>]*>|<\/span>/g, '')));
});

test('rəngləmə: bir neçə sətirlik /* */ şərhi', () => {
  const lines = T.highlightCppLines('/* a\nint b\n*/ int c');
  assert.match(lines[0], /pt-cmt/);
  assert.ok(!/pt-tp/.test(lines[1]) && /pt-cmt/.test(lines[1]));
  assert.match(lines[2], /pt-cmt">\*\/<\/span> <span class="pt-tp">int/);
});

test('yüngül yoxlama: bağlanmamış və artıq mötərizə, dırnaq', () => {
  assert.deepEqual(T.liteCheckCpp('int main(){\n  return 0;\n}\n'), {});
  assert.deepEqual(Object.keys(T.liteCheckCpp('int main(){\n  return 0;\n')), ['1']);
  assert.deepEqual(Object.keys(T.liteCheckCpp('int x;\n}\n')), ['2']);
  assert.deepEqual(Object.keys(T.liteCheckCpp('cout << "abc;\n')), ['1']);
  assert.deepEqual(T.liteCheckCpp('// ( yarımçıq\nchar c = \'(\';\n'), {});
});

test('g++ xətaları ayrılır; nöqtəli vergül xətası əvvəlki sətrə keçir', () => {
  const stderr = 'main.cpp: In function ‘int main()’:\nmain.cpp:4:5: error: expected \';\' before ‘return’\nmain.cpp:7:1: error: ‘x’ was not declared in this scope\n';
  const list = T.parseCompileErrors(stderr);
  assert.equal(list.length, 2);
  assert.equal(list[0].line, 4);
  assert.equal(list[1].message, '‘x’ was not declared in this scope');
  const text = 'int main(){\n  int a = 1\n\n    return 0;\n}\n';
  const mapped = T.mapCompilerErrors(text, [{ line: 4, column: 5, message: "expected ';' before 'return'" }]);
  assert.deepEqual(Object.keys(mapped), ['2']);
});

test('köhnə kompilyator səhvləri: düzəldilən sətirin səhvi silinir, digərləri qalır, sətir sayı dəyişəndə hamısı silinir', () => {
  const old = 'int main(){\n  int a = 1\n  cout << x;\n}\n';
  const errs = { 2: "expected ';'", 3: "'x' was not declared" };
  // 2-ci sətir düzəldilir (nöqtəli vergül əlavə olunur): yalnız o sətrin səhvi silinir
  assert.deepEqual(T.reconcileErrors(old, 'int main(){\n  int a = 1;\n  cout << x;\n}\n', errs), { 3: "'x' was not declared" });
  // dəyişiklik yoxdur
  assert.equal(T.reconcileErrors(old, old, errs), errs);
  // yeni sətir əlavə olunur: nömrələr yerini dəyişir, hamısı silinir
  assert.deepEqual(T.reconcileErrors(old, 'int main(){\n  int b;\n  int a = 1\n  cout << x;\n}\n', errs), {});
  // sətir silinir
  assert.deepEqual(T.reconcileErrors(old, 'int main(){\n  cout << x;\n}\n', errs), {});
  // 3-cü sətirdə yazı: 2-ci sətrin səhvi qalır
  assert.deepEqual(T.reconcileErrors(old, 'int main(){\n  int a = 1\n  cout << y;\n}\n', errs), { 2: "expected ';'" });
});

test('çıxış müqayisəsi yoxlayıcının qaydası ilə eynidir', () => {
  assert.equal(T.normalizeOutput('a  \r\nb\n\n'), 'a\nb');
  assert.ok(T.compareOutputs('1 2\n3\n', '1 2  \r\n3').equal);
  assert.ok(T.compareOutputs('', '\n\n').equal);
  const r = T.compareOutputs('a\nb', 'a\nc');
  assert.equal(r.equal, false);
  assert.equal(r.line, 2);
  assert.equal(T.compareOutputs('a', 'a\nb').line, 2);
});

test('qaralama açarı istifadəçi və məsələyə görə fərqlənir', () => {
  assert.notEqual(T.draftKey(1, 'frame-week1', 'A01', 'x'), T.draftKey(2, 'frame-week1', 'A01', 'x'));
  assert.notEqual(T.draftKey(1, 'frame-week1', 'A01', 'x'), T.draftKey(1, 'frame-week1', 'A02', 'x'));
});
