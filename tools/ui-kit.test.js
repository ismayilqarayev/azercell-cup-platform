// frontend-src/ui-kit.js-in sırf məntiq funksiyalarının testləri. İşə salmaq: node --test tools/*.test.js
'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const K = require('../frontend-src/ui-kit.js');

test('alert mətnindən bildiriş növü: uğursuzluq -> error', () => {
  assert.equal(K.inferType('Qrup yaratmaq mümkün olmadı.'), 'error');
  assert.equal(K.inferType('Serverə qoşulmaq mümkün olmadı'), 'error');
  assert.equal(K.inferType('Test halını silmək mümkün olmadı.'), 'error');
});

test('alert mətnindən bildiriş növü: çatışmayan sahə və gözləmə -> warn', () => {
  assert.equal(K.inferType('Qrupun adını daxil et.'), 'warn');
  assert.equal(K.inferType('Başlıq, mövzu və son tarix mütləqdir.'), 'warn');
  assert.equal(K.inferType('Ad və email boş ola bilməz.'), 'warn');
  assert.equal(K.inferType('Əvvəlcə kod yazın.'), 'warn');
  assert.equal(K.inferType('Bir azdan yenidən cəhd et (çox tez-tez yeniləmə istəyi göndərildi).'), 'warn');
  assert.equal(K.inferType('Məsələ məlumatı hələ yüklənir, bir az gözlə.'), 'warn');
});

test('alert mətnindən bildiriş növü: qalanı info, boş giriş zərərsizdir', () => {
  assert.equal(K.inferType('Kopyalandı'), 'info');
  assert.equal(K.inferType(''), 'info');
  assert.equal(K.inferType(null), 'info');
});

test('confirm: geri qaytarılmaz əməliyyatlar təhlükəli sayılır', () => {
  assert.equal(K.inferDanger('Bu qrupu silmək istəyirsən?'), true);
  assert.equal(K.inferDanger('Bu müəllim müraciətini rədd etmək istəyirsən?'), true);
  assert.equal(K.inferDanger('Bu istifadəçini TAMAMİLƏ silmək istəyirsən? Bu əməliyyat geri qaytarıla bilməz.'), true);
  assert.equal(K.inferDanger('Bu yarışı silmək istədiyinizə əminsiniz?'), true);
});

test('confirm: adi təsdiqlər təhlükəli sayılmır', () => {
  assert.equal(K.inferDanger('Kod şablona qaytarılsın? Yazdığın kod silinəcək.'), true); // kod silinir — diqqət çəkməlidir
  assert.equal(K.inferDanger('Bu istifadəçinin rolunu TEACHER olaraq dəyişmək istəyirsən?'), false);
  assert.equal(K.inferDanger('Redaktordakı kod bu göndərişin kodu ilə əvəz olunacaq. Davam edək?'), false);
  assert.equal(K.inferDanger(undefined), false);
});
