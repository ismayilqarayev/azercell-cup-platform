/* AZ-CUP ortaq interfeys alətləri: toast (alert əvəzi), confirm/prompt dialoqları, "gizli dəyər" dialoqu və ekran keçidi.
   tools/build-index.js bu faylı index.html-ə (UI_KIT:BEGIN/END) və hər iframe səhifəsinə (id="azcup-ui-kit") əlavə edir.
   Brauzerin yerli alert/confirm/prompt pəncərələri səhifəni bloklayır və dizayna uyğun gəlmir; bunlar onların asinxron əvəzidir.
   Mətnlər textContent ilə yazılır (innerHTML yox), ona görə istifadəçi məlumatı təhlükəsizdir. */
(function (root) {
  'use strict';

  /* ---------- Saf məntiq (node --test tools/ui-kit.test.js ilə yoxlanılır) ---------- */

  // alert mətninə görə rəng: uğursuzluq -> error, çatışmayan sahə/gözləmə -> warn, qalanı -> info
  function inferType(msg) {
    var s = String(msg == null ? '' : msg);
    if (/mümkün olmadı|xəta|uğursuz|alınmadı|olmadı/i.test(s)) return 'error';
    if (/mütləqdir|daxil et|boş ola bilməz|əvvəlcə|bir azdan|gözlə|yazın/i.test(s)) return 'warn';
    return 'info';
  }

  // Geri qaytarılmaz əməliyyatlar (silmək, rədd etmək) qırmızı təsdiq düyməsi alır
  function inferDanger(msg) {
    return /sil(mək|inəcək|iləcək|mə)|rədd|TAMAMİL/i.test(String(msg == null ? '' : msg));
  }

  /* ---------- DOM hissəsi ---------- */

  var SVG = {
    error: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7.5v5.5M12 16.5h.01"/></svg>',
    warn: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3.5 2.8 19.5h18.4L12 3.5z"/><path d="M12 10v4.5M12 17.2h.01"/></svg>',
    success: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="m8 12.5 2.7 2.7L16 9.8"/></svg>',
    info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 11v5.5M12 7.8h.01"/></svg>',
  };

  function doc() { return root.document; }
  function reducedMotion() {
    try { return !!root.matchMedia && root.matchMedia('(prefers-reduced-motion: reduce)').matches; } catch (e) { return false; }
  }
  function el(tag, cls, text) {
    var n = doc().createElement(tag);
    if (cls) n.className = cls;
    if (text != null) n.textContent = text;
    return n;
  }

  /* --- toast --- */
  var toastBox = null;
  function ensureToastBox() {
    if (toastBox && toastBox.isConnected) return toastBox;
    toastBox = el('div', 'az-toasts');
    toastBox.setAttribute('aria-live', 'polite');
    toastBox.setAttribute('aria-atomic', 'false');
    doc().body.appendChild(toastBox);
    return toastBox;
  }

  function toast(msg, type, opts) {
    if (!doc() || !doc().body) return;
    type = type || inferType(msg);
    opts = opts || {};
    var box = ensureToastBox();
    // eyni anda ən çox 4 bildiriş: köhnələr çıxarılır
    while (box.children.length >= 4) box.removeChild(box.firstChild);

    var ms = opts.duration || (type === 'error' ? 6500 : 4200);
    var t = el('div', 'az-toast ' + type);
    t.setAttribute('role', type === 'error' ? 'alert' : 'status');
    var ic = el('span', 'az-toast-ic');
    ic.innerHTML = SVG[type] || SVG.info; // sabit, öz yazdığımız SVG
    var m = el('div', 'az-toast-msg', msg);
    var x = el('button', 'az-toast-x', '×');
    x.type = 'button';
    x.setAttribute('aria-label', 'Bağla');
    var bar = el('span', 'az-toast-bar');
    bar.style.animationDuration = ms + 'ms';
    t.appendChild(ic); t.appendChild(m); t.appendChild(x); t.appendChild(bar);
    box.appendChild(t);

    var timer = null, left = ms, started = 0, gone = false;
    function dismiss() {
      if (gone) return; gone = true;
      clearTimeout(timer);
      t.classList.add('leaving');
      setTimeout(function () { if (t.parentNode) t.parentNode.removeChild(t); }, reducedMotion() ? 0 : 200);
    }
    function start() { started = Date.now(); timer = setTimeout(dismiss, left); bar.style.animationPlayState = 'running'; }
    function pause() { clearTimeout(timer); left -= Date.now() - started; bar.style.animationPlayState = 'paused'; }
    x.addEventListener('click', dismiss);
    t.addEventListener('mouseenter', pause);
    t.addEventListener('mouseleave', function () { if (!gone) start(); });
    start();
    return { dismiss: dismiss };
  }

  /* --- dialoq (növbəli: eyni anda yalnız biri) --- */
  var queue = [], opened = false;

  function open(cfg) {
    return new Promise(function (resolve) { queue.push({ cfg: cfg, resolve: resolve }); next(); });
  }
  function next() {
    if (opened || !queue.length || !doc() || !doc().body) return;
    opened = true;
    var job = queue.shift();
    render(job.cfg, function (value) { opened = false; job.resolve(value); next(); });
  }

  function focusables(root_) {
    return Array.prototype.slice.call(root_.querySelectorAll('button, input, textarea, select, [href], [tabindex]:not([tabindex="-1"])'))
      .filter(function (n) { return !n.disabled && n.offsetParent !== null; });
  }

  function render(cfg, done) {
    var prevFocus = doc().activeElement;
    var back = el('div', 'az-modal-backdrop');
    var box = el('div', 'az-modal' + (cfg.danger ? ' danger' : ''));
    box.setAttribute('role', cfg.kind === 'confirm' && cfg.danger ? 'alertdialog' : 'dialog');
    box.setAttribute('aria-modal', 'true');
    var titleId = 'az-modal-t-' + Math.random().toString(36).slice(2, 8);
    box.setAttribute('aria-labelledby', titleId);
    var h = el('h4', null, cfg.title || ''); h.id = titleId;
    box.appendChild(h);
    if (cfg.message) box.appendChild(el('p', null, cfg.message));

    var input = null;
    if (cfg.kind === 'prompt' || cfg.kind === 'secret') {
      input = el('input', 'az-modal-input' + (cfg.mono ? ' mono' : ''));
      input.type = 'text';
      input.value = cfg.value == null ? '' : String(cfg.value);
      input.setAttribute('aria-label', cfg.title || 'Dəyər');
      if (cfg.placeholder) input.placeholder = cfg.placeholder;
      if (cfg.kind === 'secret') input.readOnly = true;
      box.appendChild(input);
    }

    var actions = el('div', 'az-modal-actions');
    var cancel = null;
    if (!cfg.hideCancel) {
      cancel = el('button', 'az-btn ghost', cfg.cancelText || 'Ləğv et');
      cancel.type = 'button';
      actions.appendChild(cancel);
    }
    var ok = el('button', 'az-btn ' + (cfg.danger ? 'danger' : 'primary'), cfg.okText || 'Təsdiqlə');
    ok.type = 'button';
    actions.appendChild(ok);
    box.appendChild(actions);
    back.appendChild(box);
    doc().body.appendChild(back);

    var closed = false;
    function close(value) {
      if (closed) return; closed = true;
      doc().removeEventListener('keydown', onKey, true);
      back.classList.add('leaving');
      setTimeout(function () {
        if (back.parentNode) back.parentNode.removeChild(back);
        try { if (prevFocus && prevFocus.focus) prevFocus.focus(); } catch (e) { /* element artıq yoxdur */ }
        done(value);
      }, reducedMotion() ? 0 : 160);
    }
    // prompt: yazılan mətn / ləğv -> null; confirm və secret: true / false
    function accept() { close(cfg.kind === 'prompt' ? input.value : true); }
    function cancelIt() { close(cfg.kind === 'prompt' ? null : false); }
    function onKey(e) {
      if (e.key === 'Escape') { e.preventDefault(); e.stopPropagation(); cancelIt(); return; }
      if (e.key === 'Enter' && input && e.target === input && cfg.kind === 'prompt') { e.preventDefault(); accept(); return; }
      if (e.key === 'Tab') {
        var f = focusables(box);
        if (!f.length) return;
        var first = f[0], last = f[f.length - 1];
        if (e.shiftKey && doc().activeElement === first) { e.preventDefault(); last.focus(); }
        else if (!e.shiftKey && doc().activeElement === last) { e.preventDefault(); first.focus(); }
        else if (!box.contains(doc().activeElement)) { e.preventDefault(); first.focus(); }
      }
    }
    ok.addEventListener('click', accept);
    if (cancel) cancel.addEventListener('click', cancelIt);
    back.addEventListener('mousedown', function (e) { if (e.target === back && !cfg.hideCancel) cancelIt(); });
    doc().addEventListener('keydown', onKey, true);

    // Fokus: yazı sahəsi varsa ora (hamısı seçilmiş), yoxdursa təsdiq düyməsinə; təhlükəli əməliyyatda "Ləğv et"-ə
    var target = input || (cfg.danger && cancel ? cancel : ok);
    setTimeout(function () { try { target.focus(); if (input) input.select(); } catch (e) { /* yoxdur */ } }, 30);
  }

  function confirmDialog(msg, opts) {
    opts = opts || {};
    var danger = opts.danger != null ? opts.danger : inferDanger(msg);
    return open({
      kind: 'confirm', message: String(msg), danger: danger,
      title: opts.title || (danger ? 'Əminsən?' : 'Təsdiq'),
      okText: opts.okText || (danger ? 'Bəli, davam et' : 'Təsdiqlə'), cancelText: opts.cancelText,
    });
  }
  function promptDialog(msg, value, opts) {
    opts = opts || {};
    return open({
      kind: 'prompt', message: String(msg), value: value, title: opts.title || 'Dəyər daxil et',
      okText: opts.okText || 'Yadda saxla', placeholder: opts.placeholder, mono: opts.mono,
    });
  }
  function copyText(text) {
    try {
      if (root.navigator && root.navigator.clipboard && root.navigator.clipboard.writeText) {
        return root.navigator.clipboard.writeText(text).then(function () { return true; }, function () { return false; });
      }
    } catch (e) { /* aşağıdakı ehtiyat yol */ }
    try {
      var ta = el('textarea'); ta.value = text; ta.style.position = 'fixed'; ta.style.opacity = '0';
      doc().body.appendChild(ta); ta.select();
      var okc = doc().execCommand('copy'); doc().body.removeChild(ta);
      return Promise.resolve(!!okc);
    } catch (e2) { return Promise.resolve(false); }
  }

  // Bir dəfə göstərilən dəyər (məs. yeni parol): "Kopyala və bağla" basılanda buferə yazılır, "Yalnız bağla" heç nə etmir
  function secret(msg, value, opts) {
    return open({
      kind: 'secret', message: String(msg), value: value, title: (opts && opts.title) || 'Bir daha göstərilməyəcək',
      okText: 'Kopyala və bağla', cancelText: 'Yalnız bağla', mono: true,
    }).then(function (accepted) {
      if (accepted === true) {
        return copyText(value).then(function (okc) { if (okc) toast('Kopyalandı', 'success', { duration: 2200 }); });
      }
    });
  }

  // Ekran keçidi: köhnə hissə yumşaq çıxır, yenisi yumşaq girir (azaldılmış hərəkətdə dərhal)
  function swap(out, inn, display) {
    display = display || 'block';
    function show() {
      if (!inn) return;
      inn.style.display = display;
      if (reducedMotion()) return;
      inn.classList.remove('az-in'); void inn.offsetWidth; inn.classList.add('az-in');
    }
    if (!out || out === inn || out.style.display === 'none' || reducedMotion()) {
      if (out && out !== inn) out.style.display = 'none';
      show(); return;
    }
    out.classList.add('az-out');
    setTimeout(function () { out.classList.remove('az-out'); out.style.display = 'none'; show(); }, 150);
  }

  // Uzun səhifələrdə (Mövzu testləri) bir qədər sürüşəndən sonra görünən "yuxarı" düyməsi. Yalnız iframe səhifələrində (.wrap olanda).
  function initBackToTop() {
    var d = doc();
    if (!d || !d.body || root.self === root.top || !d.querySelector('.wrap')) return;
    var btn = el('button', 'az-top');
    btn.type = 'button';
    btn.setAttribute('aria-label', 'Yuxarı qayıt');
    btn.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 19V5M5 12l7-7 7 7"/></svg>';
    d.body.appendChild(btn);
    function update() { btn.classList.toggle('show', (root.scrollY || d.documentElement.scrollTop) > 700); }
    root.addEventListener('scroll', update, { passive: true });
    btn.addEventListener('click', function () { root.scrollTo({ top: 0, behavior: reducedMotion() ? 'auto' : 'smooth' }); });
    update();
  }
  if (doc()) {
    if (doc().readyState === 'loading') doc().addEventListener('DOMContentLoaded', initBackToTop, { once: true });
    else initBackToTop();
  }

  var api = {
    toast: toast,
    alert: function (msg) { return toast(msg); },
    confirm: confirmDialog,
    prompt: promptDialog,
    secret: secret,
    swap: swap,
    reducedMotion: reducedMotion,
    inferType: inferType,
    inferDanger: inferDanger,
  };

  root.AzcupUI = api;
  if (typeof module !== 'undefined' && module.exports) module.exports = { inferType: inferType, inferDanger: inferDanger };
})(typeof window !== 'undefined' ? window : globalThis);
