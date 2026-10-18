/* Məsələ həlli sahəsinin (Mövzu 1–5 səhifələri) paylaşılan təkmilləşdirmələri.
 *
 * Bu fayl tools/build-index.js tərəfindən index.html-ə (PROBLEM_TOOLS:BEGIN/END arasına) kopyalanır və
 * parent səhifədə window.AzcupProblemTools kimi işləyir. weekN.html səhifələrinin özünə TOXUNULMUR:
 * iframe yüklənəndə attachFrame() çağırılır, #problems siyahısındakı hər .problem kartı DOM-dan tapılıb
 * təkmilləşdirilir (eyni id-lər: problem-N, code-N, gutter-N, submit-btn-N, verdict-N ...).
 *
 *   1. Redaktor: Tab = 4 boşluq (çox sətirdə girinti/çıxıntı), avtomatik girinti, mötərizə/dırnaq cütləmə,
 *      rəngləmə (Monokai), səhv sətirlərinin qırmızı göstərilməsi (yüngül yoxlama + g++ -fsyntax-only).
 *   2. Qaralama: kod hər dəyişəndə localStorage-a yazılır, səhifə yenilənəndə bərpa olunur.
 *   3. "Nümunə ilə sına" və "Öz giriş ilə işə sal" (/api/run) — göndərmədən əvvəl yoxlamaq üçün.
 *   5. Mərhələli köməklər: teqlər və "Yanaşma" gizlidir, "💡" düyməsi ilə addım-addım açılır (həll edilibsə və ya
 *      müəllim/admin üçün hamısı açıqdır). Açılmış səviyyə istifadəçi və məsələ üzrə yadda qalır.
 *   6. Tarixçə: hər göndərişin kodunu göstərir və "Redaktora yüklə" ilə redaktora qaytarır (API kodu artıq qaytarırdı).
 *   7. Siyahı: irəliləyiş zolağı, axtarış (azərbaycan hərflərinə dözümlü), çətinlik/vəziyyət filtri, "Növbəti həll edilməmiş".
 *   8. Klaviatura: Ctrl+Enter = göndər, Ctrl+Shift+Enter = nümunə ilə sına, Esc sonra Tab = redaktordan çıx.
 *
 * Sırf məntiq funksiyaları (editForKey, highlightCppLines, liteCheckCpp, ...) DOM-dan asılı deyil və
 * tools/problem-tools.test.js ilə Node-da yoxlanılır.
 */
(function (root) {
  'use strict';

  var INDENT_SIZE = 4;
  var INDENT = '    ';
  var PAIRS = { '(': ')', '[': ']', '{': '}', '"': '"', "'": "'" };
  var WORD_RE = /[A-Za-z0-9_]/;

  /* ---------------------------------------------------------------- sırf məntiq */

  function escapeHtml(s) {
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function lineStartOf(value, pos) {
    return value.lastIndexOf('\n', pos - 1) + 1;
  }

  // Klaviş basılanda mətndə nə dəyişməlidir? Dəyişiklik lazım deyilsə (brauzerin adi davranışı) null qaytarır.
  // Cavab: { start, end, text, selStart, selEnd, skip? } — value[start:end] əvəz olunur, sonra seçim qoyulur.
  // skip=true: mətn dəyişmir, yalnız kursor qapanan simvolun üstündən keçir.
  function editForKey(value, start, end, key, mods) {
    mods = mods || {};
    var shift = !!mods.shift;
    var hasSel = start !== end;
    var ls = lineStartOf(value, start);

    if (key === 'Tab') {
      var multi = hasSel && value.slice(start, end).indexOf('\n') !== -1;
      if (!multi && !shift) {
        var col = start - ls;
        var n = INDENT_SIZE - (col % INDENT_SIZE);
        var sp = new Array(n + 1).join(' ');
        return { start: start, end: end, text: sp, selStart: start + n, selEnd: start + n };
      }
      // Sətir əsaslı girinti/çıxıntı: seçimin toxunduğu bütün sətirlər.
      var blockStart = ls;
      var blockEnd;
      if (hasSel && value.charAt(end - 1) === '\n') blockEnd = end - 1; // seçim növbəti sətrin əvvəlində bitir — onu daxil etmə
      else { var nl = value.indexOf('\n', end); blockEnd = nl === -1 ? value.length : nl; }
      var lines = value.slice(blockStart, blockEnd).split('\n');
      var removedFirst = 0, totalDelta = 0;
      var out = lines.map(function (ln, idx) {
        if (!shift) {
          if (ln.trim() === '') return ln;
          if (idx === 0) removedFirst = -INDENT_SIZE;
          totalDelta += INDENT_SIZE;
          return INDENT + ln;
        }
        var m = /^( {1,4}|\t)/.exec(ln);
        if (!m) return ln;
        if (idx === 0) removedFirst = m[0].length;
        totalDelta -= m[0].length;
        return ln.slice(m[0].length);
      });
      var text = out.join('\n');
      var selStart = shift ? Math.max(blockStart, start - removedFirst) : start + (removedFirst ? INDENT_SIZE : 0);
      var selEnd = Math.max(selStart, end + totalDelta);
      return { start: blockStart, end: blockEnd, text: text, selStart: selStart, selEnd: selEnd };
    }

    if (key === 'Enter') {
      var cur = value.slice(ls, start);
      var baseIndent = (/^\s*/.exec(cur) || [''])[0];
      var opens = /[{([]\s*$/.test(cur);
      // {|} → {\n    |\n}
      if (!hasSel && opens && /^[)\]}]/.test(value.slice(end))) {
        var ins = '\n' + baseIndent + INDENT + '\n' + baseIndent;
        var caret = start + 1 + baseIndent.length + INDENT_SIZE;
        return { start: start, end: end, text: ins, selStart: caret, selEnd: caret };
      }
      var ind = '\n' + baseIndent + (opens ? INDENT : '');
      return { start: start, end: end, text: ind, selStart: start + ind.length, selEnd: start + ind.length };
    }

    if (key === 'Backspace' && !hasSel && start > 0) {
      var a = value.charAt(start - 1), b = value.charAt(start);
      if (PAIRS[a] && PAIRS[a] === b) {
        return { start: start - 1, end: start + 1, text: '', selStart: start - 1, selEnd: start - 1 };
      }
      return null;
    }

    if (key === ')' || key === ']' || key === '}') {
      if (hasSel) return null;
      if (value.charAt(start) === key) return { start: start, end: start, text: '', selStart: start + 1, selEnd: start + 1, skip: true };
      if (key === '}') {
        var before = value.slice(ls, start);
        if (/^ +$/.test(before)) {
          var k = Math.min(INDENT_SIZE, before.length);
          return { start: start - k, end: start, text: '}', selStart: start - k + 1, selEnd: start - k + 1 };
        }
      }
      return null;
    }

    if (key === '"' || key === "'") {
      if (hasSel) {
        var sel = value.slice(start, end);
        return { start: start, end: end, text: key + sel + key, selStart: start + 1, selEnd: end + 1 };
      }
      if (value.charAt(start) === key) return { start: start, end: start, text: '', selStart: start + 1, selEnd: start + 1, skip: true };
      var prev = value.charAt(start - 1), next = value.charAt(start);
      // sözün içində (don't, 1'000) və ya hərfin yanında cütləmə: adi simvol yazılır
      if (WORD_RE.test(prev) || WORD_RE.test(next)) return null;
      return { start: start, end: end, text: key + key, selStart: start + 1, selEnd: start + 1 };
    }

    if (key === '(' || key === '[' || key === '{') {
      var closer = PAIRS[key];
      if (hasSel) {
        var s2 = value.slice(start, end);
        return { start: start, end: end, text: key + s2 + closer, selStart: start + 1, selEnd: end + 1 };
      }
      var nx = value.charAt(start);
      if (nx === '' || /[\s)\]};,]/.test(nx)) {
        return { start: start, end: end, text: key + closer, selStart: start + 1, selEnd: start + 1 };
      }
      return null;
    }
    return null;
  }

  // C++ rəngləməsi (Monokai): sətir-sətir HTML. /* ... */ bir neçə sətrə yayılanda vəziyyət saxlanılır.
  var TOKENS = [
    ['pt-cmt', '\\/\\/.*$'],
    ['pt-cmt', '\\/\\*.*?\\*\\/'],
    ['pt-cmt-open', '\\/\\*.*$'],
    ['pt-str', '"(?:[^"\\\\]|\\\\.)*(?:"|$)|\'(?:[^\'\\\\]|\\\\.)*(?:\'|$)'],
    ['pt-pp', '#\\s*include\\s*(?:<[^>\\n]*>|"[^"\\n]*")|#\\s*[A-Za-z_]+'],
    ['pt-num', '\\b\\d+(?:\\.\\d+)?\\b'],
    ['pt-kw', '\\b(?:for|while|if|else|return|break|continue|switch|case|default|struct|class|public|private|protected|using|namespace|const|auto|static|void|do|new|delete|template|typename|typedef|cin|cout|endl|true|false|nullptr|sizeof)\\b'],
    ['pt-tp', '\\b(?:int|long|double|float|char|bool|string|vector|map|unordered_map|unordered_set|set|queue|stack|pair|size_t|short|unsigned|signed)\\b'],
  ];
  var TOKEN_RE_SRC = TOKENS.map(function (t) { return '(' + t[1] + ')'; }).join('|');

  function span(cls, text) { return '<span class="' + cls + '">' + escapeHtml(text) + '</span>'; }

  function highlightCppLines(text) {
    var lines = String(text).split('\n');
    var inBlock = false;
    return lines.map(function (line) {
      var rest = line, out = '';
      if (inBlock) {
        var e = rest.indexOf('*/');
        if (e === -1) return span('pt-cmt', rest);
        out += span('pt-cmt', rest.slice(0, e + 2));
        rest = rest.slice(e + 2);
        inBlock = false;
      }
      var re = new RegExp(TOKEN_RE_SRC, 'g');
      var last = 0, m;
      while ((m = re.exec(rest)) !== null) {
        if (m[0] === '') { re.lastIndex++; continue; }
        out += escapeHtml(rest.slice(last, m.index));
        var cls = 'pt-cmt';
        for (var g = 0; g < TOKENS.length; g++) { if (m[g + 1] !== undefined) { cls = TOKENS[g][0]; break; } }
        if (cls === 'pt-cmt-open') { inBlock = true; cls = 'pt-cmt'; }
        out += span(cls, m[0]);
        last = re.lastIndex;
      }
      out += escapeHtml(rest.slice(last));
      return out;
    });
  }

  // Brauzerdə dərhal işləyən yüngül yoxlama: bağlanmamış mötərizə/dırnaq. { sətirNömrəsi: mesaj }
  function liteCheckCpp(text) {
    var errors = {};
    if (!text.trim() || /R"[^(\s]*\(/.test(text)) return errors; // raw string — dəqiq təhlil etmirik
    var stack = [];
    var pairs = { ')': '(', ']': '[', '}': '{' };
    var line = 1, i = 0, inBlock = false, n = text.length;
    function add(ln, msg) { if (!errors[ln] && Object.keys(errors).length < 5) errors[ln] = msg; }
    while (i < n) {
      var c = text[i];
      if (c === '\n') { line++; i++; continue; }
      if (inBlock) { if (c === '*' && text[i + 1] === '/') { inBlock = false; i += 2; } else i++; continue; }
      if (c === '/' && text[i + 1] === '/') { while (i < n && text[i] !== '\n') i++; continue; }
      if (c === '/' && text[i + 1] === '*') { inBlock = true; i += 2; continue; }
      if (c === '"') {
        var j = i + 1;
        while (j < n && text[j] !== '"' && text[j] !== '\n') j += text[j] === '\\' ? 2 : 1;
        if (j >= n || text[j] === '\n') { add(line, 'Dırnaq (") bağlanmayıb'); i = j; continue; }
        i = j + 1; continue;
      }
      if (c === "'") {
        var mm = /^'(?:\\.[^'\n]*|[^'\\\n])'/.exec(text.slice(i, i + 12));
        i += mm ? mm[0].length : 1;
        continue;
      }
      if (c === '(' || c === '[' || c === '{') stack.push({ ch: c, line: line });
      else if (c === ')' || c === ']' || c === '}') {
        if (stack.length && stack[stack.length - 1].ch === pairs[c]) stack.pop();
        else add(line, "Artıq '" + c + "' — bağladığı mötərizə açılmayıb");
      }
      i++;
    }
    for (var s = 0; s < stack.length; s++) add(stack[s].line, "'" + stack[s].ch + "' bağlanmayıb");
    return errors;
  }

  // g++ çıxışından səhv siyahısı: "main.cpp:SƏTİR:SÜTUN: error: MESAJ"
  function parseCompileErrors(stderr) {
    var out = [];
    var re = /^[^\n:]*:(\d+):(\d+):\s*(?:fatal )?error:\s*(.+)$/gm;
    var m;
    while ((m = re.exec(String(stderr || ''))) !== null && out.length < 10) {
      out.push({ line: parseInt(m[1], 10), column: parseInt(m[2], 10), message: m[3].trim() });
    }
    return out;
  }

  // g++ nöqtəli vergül çatışmayanda səhvi NÖVBƏTİ sətirdə göstərir — işarəni əvvəlki boş olmayan sətrə keçirir.
  function mapCompilerErrors(text, list) {
    var lines = text.split('\n');
    var out = {};
    (list || []).forEach(function (e) {
      var ln = e.line;
      var startsLine = lines[ln - 1] !== undefined && lines[ln - 1].slice(0, Math.max(0, e.column - 1)).trim() === '';
      if (startsLine && /expected .*';'/.test(e.message)) {
        var p = ln - 2;
        while (p >= 0 && !lines[p].trim()) p--;
        if (p >= 0) ln = p + 1;
      }
      if (!out[ln]) out[ln] = e.message;
    });
    return out;
  }

  // Kod dəyişəndə köhnə kompilyator səhvlərini nə qədər saxlamalı? Yalnız bir sətir daxilində dəyişiklik olubsa, toxunulmayan
  // sətirlərin səhvləri qalır (nömrələr eyni qalır); düzəldilən sətrin səhvi dərhal silinir. Sətir sayı dəyişibsə,
  // nömrələr yerini dəyişir — hamısı silinir (səhv hələ də varsa, növbəti yoxlama onu yenidən göstərəcək).
  function reconcileErrors(oldText, newText, errors) {
    if (oldText === newText) return errors;
    var a = String(oldText), b = String(newText);
    var max = Math.min(a.length, b.length), p = 0;
    while (p < max && a.charCodeAt(p) === b.charCodeAt(p)) p++;
    var s = 0, maxS = max - p;
    while (s < maxS && a.charCodeAt(a.length - 1 - s) === b.charCodeAt(b.length - 1 - s)) s++;
    if (a.split('\n').length !== b.split('\n').length) return {};
    var first = a.slice(0, p).split('\n').length;
    var lastOld = a.slice(0, a.length - s).split('\n').length;
    var out = {};
    Object.keys(errors || {}).forEach(function (k) {
      var n = +k;
      if (n < first || n > lastOld) out[k] = errors[k];
    });
    return out;
  }

  // Yoxlayıcının qaydası ilə eyni: sətir sonları, sondakı boş sətirlər, sətir sonundakı boşluqlar nəzərə alınmır.
  function normalizeOutput(s) {
    return String(s == null ? '' : s).replace(/\r\n?/g, '\n').split('\n').map(function (l) { return l.replace(/\s+$/, ''); }).join('\n').replace(/\n+$/, '');
  }

  function compareOutputs(expected, actual) {
    var e = normalizeOutput(expected).split('\n'), a = normalizeOutput(actual).split('\n');
    var n = Math.max(e.length, a.length);
    for (var i = 0; i < n; i++) {
      if (e[i] !== a[i]) return { equal: false, line: i + 1, expected: e, actual: a };
    }
    return { equal: true, line: 0, expected: e, actual: a };
  }

  function draftKey(userId, frameId, num, title) {
    return 'azcup.draft.v1.' + userId + '.' + frameId + '.' + num + '.' + title;
  }

  function hintKey(userId, frameId, num, title) {
    return 'azcup.hint.v1.' + userId + '.' + frameId + '.' + num + '.' + title;
  }

  // Axtarış üçün: kiçik hərf, azərbaycan hərfləri latın qarşılığına (ə→e, ı→i, ö→o, ü→u, ş→s, ç→c, ğ→g), ki
  // "ededler" yazan da "ədədlər"i tapsın.
  function foldAz(s) {
    var t;
    try { t = String(s == null ? '' : s).toLocaleLowerCase('az'); } catch (e) { t = String(s == null ? '' : s).toLowerCase(); }
    return t.replace(/̇/g, '').replace(/ə/g, 'e').replace(/ı/g, 'i').replace(/ö/g, 'o').replace(/ü/g, 'u')
      .replace(/ş/g, 's').replace(/ç/g, 'c').replace(/ğ/g, 'g');
  }

  // Sorğunun bütün sözləri saman mətnində (nömrə + başlıq + teqlər, artıq foldAz-dan keçmiş) olmalıdır.
  function matchQuery(haystack, query) {
    var tokens = foldAz(query).split(/\s+/).filter(Boolean);
    for (var i = 0; i < tokens.length; i++) if (haystack.indexOf(tokens[i]) === -1) return false;
    return true;
  }

  /* ---------------------------------------------------------------- DOM hissəsi */

  var STYLE_ID = 'azcup-pt-style';
  var CSS = [
    '.pt-editor{ position:relative; border-radius:4px; transition:box-shadow 0.15s ease; }',
    '.pt-editor:focus-within{ box-shadow:0 0 0 1px var(--gold), 0 0 16px rgba(38,210,255,0.12); }',
    '.pt-hl{ position:absolute; top:0; left:0; right:0; bottom:0; margin:0; z-index:1; font-family:var(--mono); font-size:12.5px; line-height:1.5;',
    '  tab-size:4; color:#f8f8f2; background:#272822; border:1px solid var(--grid-line-strong); border-radius:4px;',
    '  padding:10px 12px 26px 44px; white-space:pre; overflow:hidden; pointer-events:none; box-sizing:border-box; }',
    '.pt-editor:focus-within .pt-hl{ border-color:var(--gold); }',
    '.pt-editor .code-gutter{ z-index:3; background:rgba(0,0,0,0.18); color:#9b9784; }',
    '.pt-editor .code-input{ display:block; position:relative; z-index:4; background:transparent; color:transparent; caret-color:#f8f8f2; border-color:transparent; tab-size:4; }',
    '.pt-editor .code-input:focus{ border-color:transparent; }',
    '.pt-editor .code-input::selection{ background:rgba(38,210,255,0.28); }',
    '.pt-cmt{ color:#9a967f; font-style:italic; } .pt-str{ color:#e6db74; } .pt-pp{ color:#f92672; } .pt-num{ color:#ae81ff; }',
    '.pt-kw{ color:#f92672; } .pt-tp{ color:#66d9ef; font-style:italic; }',
    '.pt-err{ text-decoration:underline wavy #ff5555; text-decoration-skip-ink:none; background:rgba(255,59,59,0.18); }',
    '.pt-gut-err{ color:#ff5555; font-weight:700; }',
    '.pt-meta{ display:flex; justify-content:space-between; align-items:flex-start; gap:6px 14px; flex-wrap:wrap; margin-top:6px;',
    '  font-family:var(--mono); font-size:12px; line-height:1.5; color:var(--ink-soft); }',
    '.pt-check{ min-width:0; } .pt-check.bad{ color:#ff6b6b; } .pt-check.ok{ color:var(--green); }',
    '.pt-hint{ color:var(--ink-soft); }',
    '.pt-link{ font-family:var(--mono); font-size:12px; background:none; border:none; padding:0; margin:0; color:var(--gold); cursor:pointer; text-decoration:underline; }',
    '.pt-btn{ font-family:var(--mono); font-size:12px; font-weight:700; letter-spacing:0.3px; background:transparent; color:var(--ink);',
    '  border:1px solid var(--gold); border-radius:20px; padding:7px 14px; cursor:pointer; transition:background 0.15s ease, color 0.15s ease; }',
    '.pt-btn:hover{ background:rgba(63,196,222,0.14); }',
    '.pt-btn.ghost{ border-color:var(--grid-line-strong); color:var(--ink-soft); font-weight:400; }',
    '.pt-btn.ghost:hover{ border-color:var(--gold); color:var(--ink); }',
    '.pt-btn:disabled{ opacity:0.6; cursor:default; }',
    '.pt-btn:focus-visible, .pt-link:focus-visible{ outline:2px solid var(--gold); outline-offset:2px; }',
    '.pt-custom{ display:none; margin-top:10px; } .pt-custom.show{ display:block; }',
    '.pt-stdin{ width:100%; box-sizing:border-box; min-height:64px; font-family:var(--mono); font-size:12.5px; line-height:1.5; color:var(--ink);',
    '  background:var(--paper); border:1px solid var(--grid-line-strong); border-radius:4px; padding:8px 10px; resize:vertical; outline:none; margin:6px 0 8px; }',
    '.pt-stdin:focus{ border-color:var(--gold); }',
    '.pt-run{ display:none; margin-top:10px; } .pt-run.show{ display:block; }',
    '.pt-run .verdict-badge{ margin-bottom:8px; }',
    '.pt-run-time{ font-family:var(--mono); font-size:12px; color:var(--ink-soft); margin-left:8px; white-space:nowrap; display:inline-block; margin-bottom:8px; }',
    '.pt-run pre{ max-height:260px; overflow:auto; }',
    '.pt-diff{ background:rgba(255,59,59,0.22); color:inherit; display:inline-block; width:100%; }',
    '.pt-label{ font-family:var(--mono); font-size:12px; color:var(--ink-soft); }',
    '.pt-hide{ display:none !important; }',
    // Uzun kod/çıxış sətri (white-space:pre) grid elementini genişləndirib kartı kənardan çıxarmasın.
    '.problem-left, .problem-right{ min-width:0; }',
    '.pt-toolbar{ margin:0 0 16px; padding:14px; background:var(--paper-warm); border:1px solid var(--grid-line-strong); border-radius:6px; }',
    '.pt-prog{ display:flex; align-items:center; gap:12px; margin-bottom:12px; font-family:var(--mono); font-size:12px; color:var(--ink-soft); }',
    '.pt-prog-text{ white-space:nowrap; color:var(--ink); }',
    '.pt-prog-bar{ flex:1; height:6px; border-radius:3px; background:var(--grid-line); overflow:hidden; }',
    '.pt-prog-bar i{ display:block; height:100%; width:0; background:var(--green); transition:width 0.3s ease; }',
    '.pt-filters{ display:flex; flex-wrap:wrap; align-items:center; gap:10px; }',
    '.pt-search{ flex:1 1 220px; min-width:0; font-family:var(--sans); font-size:14px; color:var(--ink); background:var(--paper);',
    '  border:1px solid var(--grid-line-strong); border-radius:20px; padding:8px 14px; outline:none; }',
    '.pt-search:focus{ border-color:var(--gold); }',
    '.pt-chips{ display:flex; gap:6px; flex-wrap:wrap; }',
    '.pt-chip{ font-family:var(--mono); font-size:12px; color:var(--ink-soft); background:transparent; border:1px solid var(--grid-line-strong);',
    '  border-radius:16px; padding:6px 12px; cursor:pointer; }',
    '.pt-chip:hover{ border-color:var(--gold); color:var(--ink); }',
    '.pt-chip[aria-pressed="true"]{ background:rgba(63,196,222,0.16); border-color:var(--gold); color:var(--ink); }',
    '.pt-chip:focus-visible{ outline:2px solid var(--gold); outline-offset:2px; }',
    '.pt-count{ margin-top:10px; font-family:var(--mono); font-size:12px; color:var(--ink-soft); } .pt-count:empty{ display:none; }',
    '.pt-empty{ display:none; padding:18px; text-align:center; color:var(--ink-soft); font-size:14px; } .pt-empty.show{ display:block; }',
    '.pt-hints{ margin:12px 0; padding:10px 12px; border:1px dashed var(--grid-line-strong); border-radius:6px; display:flex; align-items:center; gap:10px 14px; flex-wrap:wrap; }',
    '.pt-hint-note{ font-family:var(--mono); font-size:12px; color:var(--ink-soft); }',
    '.pt-hist-row{ border-bottom:1px solid var(--grid-line); padding:4px 0 6px; } .pt-hist-row:last-child{ border-bottom:none; }',
    '.pt-hist-actions{ display:flex; gap:14px; padding:0 10px 2px; }',
    '.pt-hist-code{ display:none; margin:6px 10px 4px; padding:10px 12px; background:#272822; color:#f8f8f2; border:1px solid var(--grid-line-strong);',
    '  border-radius:4px; font-family:var(--mono); font-size:12.5px; line-height:1.5; tab-size:4; white-space:pre; overflow:auto; max-height:280px; }',
    '.pt-hist-code.show{ display:block; }',
    '@media (max-width:560px){ .pt-keys{ display:none; } }',
  ].join('\n');

  var RUN_LABELS = {
    ACCEPTED: '✓ İCRA OLUNDU',
    COMPILE_ERROR: '⚠ KOMPİLYASİYA XƏTASI',
    RUNTIME_ERROR: '⚠ İCRA XƏTASI',
    TIME_LIMIT_EXCEEDED: '⏱ VAXT LİMİTİ AŞILDI',
  };

  function injectStyles(doc) {
    if (doc.getElementById(STYLE_ID)) return;
    var st = doc.createElement('style');
    st.id = STYLE_ID;
    st.textContent = CSS;
    doc.head.appendChild(st);
  }

  function getUser() {
    try { return root.AzcupAuth && root.AzcupAuth.getUser(); } catch (e) { return null; }
  }

  // Təsdiq dialoqu: iframe-in öz AzcupUI.confirm-i (bax: ui-kit.js); yoxdursa brauzerin yerli confirm-i. Həmişə Promise<boolean>.
  function ask(win, message, opts) {
    try { if (win.AzcupUI && win.AzcupUI.confirm) return win.AzcupUI.confirm(message, opts); } catch (e) { /* aşağıdakı ehtiyat yol */ }
    return Promise.resolve(win.confirm(message));
  }

  function attachFrame(frameEl) {
    var doc, win;
    try { doc = frameEl.contentDocument; win = frameEl.contentWindow; } catch (e) { return; }
    if (!doc || !win || !doc.getElementById('problems')) return; // yalnız məsələ səhifələri
    if (doc.documentElement.getAttribute('data-pt') === '1') return;
    doc.documentElement.setAttribute('data-pt', '1');
    injectStyles(doc);
    var container = doc.getElementById('problems');
    var toolbar = buildToolbar(doc, win, container);
    function scan() {
      var list = container.querySelectorAll('.problem');
      for (var i = 0; i < list.length; i++) enhanceProblem(frameEl, doc, win, list[i]);
      toolbar.refresh();
    }
    new win.MutationObserver(scan).observe(container, { childList: true });
    // "✓ Həll edilib" nişanı səhifənin öz kodu tərəfindən (style.display) göstərilir — irəliləyişi və filtri yeniləyir.
    var refreshTimer = null;
    new win.MutationObserver(function () {
      clearTimeout(refreshTimer);
      refreshTimer = setTimeout(toolbar.refresh, 60);
    }).observe(container, { subtree: true, attributes: true, attributeFilter: ['style'] });
    scan();
  }

  // Məsələ siyahısının üstündə: irəliləyiş zolağı, axtarış, çətinlik/vəziyyət filtri, "Növbəti həll edilməmiş".
  function buildToolbar(doc, win, container) {
    var bar = doc.createElement('div');
    bar.className = 'pt-toolbar';
    bar.innerHTML = '<div class="pt-prog"><span class="pt-prog-text"></span><span class="pt-prog-bar" aria-hidden="true"><i></i></span></div>'
      + '<div class="pt-filters">'
      + '<input type="search" class="pt-search" placeholder="Axtar: başlıq, nömrə, teq…" aria-label="Məsələ axtar" autocomplete="off" spellcheck="false">'
      + '<div class="pt-chips" role="group" aria-label="Çətinlik">'
      + '<button type="button" class="pt-chip" data-diff="all" aria-pressed="true">Hamısı</button>'
      + '<button type="button" class="pt-chip" data-diff="easy" aria-pressed="false">Asan</button>'
      + '<button type="button" class="pt-chip" data-diff="mid" aria-pressed="false">Orta</button></div>'
      + '<div class="pt-chips" role="group" aria-label="Vəziyyət">'
      + '<button type="button" class="pt-chip" data-stat="all" aria-pressed="true">Hamısı</button>'
      + '<button type="button" class="pt-chip" data-stat="todo" aria-pressed="false">Həll edilməyib</button>'
      + '<button type="button" class="pt-chip" data-stat="done" aria-pressed="false">Həll edilib</button></div>'
      + '<button type="button" class="pt-btn ghost pt-next">Növbəti həll edilməmiş →</button>'
      + '</div><div class="pt-count" aria-live="polite"></div>';
    var empty = doc.createElement('div');
    empty.className = 'pt-empty';
    empty.innerHTML = 'Heç bir məsələ tapılmadı. <button type="button" class="pt-link pt-clear">Filtrləri sıfırla</button>';
    container.parentElement.insertBefore(bar, container);
    container.parentElement.insertBefore(empty, container);

    var state = { q: '', diff: 'all', stat: 'all' };
    var search = bar.querySelector('.pt-search');
    var progText = bar.querySelector('.pt-prog-text');
    var progBar = bar.querySelector('.pt-prog-bar i');
    var countEl = bar.querySelector('.pt-count');

    function info(el) {
      if (!el.__pt) {
        var numEl = el.querySelector('.problem-num'), titleEl = el.querySelector('h3'), tags = el.querySelectorAll('.tag');
        var tagText = '';
        for (var i = 0; i < tags.length; i++) tagText += ' ' + tags[i].textContent;
        var dEl = el.querySelector('.difficulty');
        el.__pt = {
          hay: foldAz((numEl ? numEl.textContent : '') + ' ' + (titleEl ? titleEl.textContent : '') + tagText),
          diff: dEl ? (/\beasy\b/.test(dEl.className) ? 'easy' : (/\bmid\b/.test(dEl.className) ? 'mid' : 'other')) : 'other',
        };
      }
      return el.__pt;
    }
    function isSolved(el) {
      var sb = doc.getElementById('solved-' + el.id.replace('problem-', ''));
      return !!sb && sb.style.display !== 'none';
    }

    function refresh() {
      var probs = container.querySelectorAll('.problem');
      var total = probs.length, solved = 0, shown = 0;
      for (var i = 0; i < probs.length; i++) {
        var el = probs[i], inf = info(el), done = isSolved(el);
        if (done) solved++;
        var ok = matchQuery(inf.hay, state.q)
          && (state.diff === 'all' || inf.diff === state.diff)
          && (state.stat === 'all' || (state.stat === 'done' ? done : !done));
        el.classList.toggle('pt-hide', !ok);
        if (ok) shown++;
      }
      progText.textContent = solved + '/' + total + ' həll edilib';
      progBar.style.width = (total ? Math.round(solved * 100 / total) : 0) + '%';
      countEl.textContent = shown !== total ? 'Göstərilir: ' + shown + '/' + total : '';
      empty.classList.toggle('show', total > 0 && shown === 0);
    }

    search.addEventListener('input', function () { state.q = search.value; refresh(); });
    function bindChips(attr, key) {
      var chips = bar.querySelectorAll('.pt-chip[' + attr + ']');
      for (var i = 0; i < chips.length; i++) {
        chips[i].addEventListener('click', function () {
          state[key] = this.getAttribute(attr);
          for (var j = 0; j < chips.length; j++) chips[j].setAttribute('aria-pressed', chips[j] === this ? 'true' : 'false');
          refresh();
        });
      }
    }
    bindChips('data-diff', 'diff');
    bindChips('data-stat', 'stat');
    empty.querySelector('.pt-clear').addEventListener('click', function () {
      state.q = ''; state.diff = 'all'; state.stat = 'all'; search.value = '';
      var chips = bar.querySelectorAll('.pt-chip');
      for (var i = 0; i < chips.length; i++) chips[i].setAttribute('aria-pressed', /^(all)$/.test(chips[i].getAttribute('data-diff') || chips[i].getAttribute('data-stat')) ? 'true' : 'false');
      refresh();
    });
    bar.querySelector('.pt-next').addEventListener('click', function () {
      var probs = container.querySelectorAll('.problem');
      for (var i = 0; i < probs.length; i++) {
        var el = probs[i];
        if (el.classList.contains('pt-hide') || isSolved(el)) continue;
        if (!el.classList.contains('expanded') && typeof win.toggleProblem === 'function') win.toggleProblem(parseInt(el.id.replace('problem-', ''), 10));
        el.scrollIntoView({ block: 'start', behavior: 'smooth' });
        return;
      }
      countEl.textContent = 'Bu filtrdə həll edilməmiş məsələ yoxdur.';
    });
    return { refresh: refresh };
  }

  // Məsələnin ID-sini səhifənin öz məlumatından tapır (Mövzu 1: problemIdByKey, Mövzu 2–5: problemIdByTitle).
  function lookupProblemId(win, title) {
    try {
      return win.eval('(function(t){'
        + 'if (typeof problemIdByTitle !== "undefined" && problemIdByTitle[t]) return problemIdByTitle[t].id;'
        + 'if (typeof problemIdByKey !== "undefined" && typeof PROBLEM_SETS !== "undefined") {'
        + '  var k = PROBLEM_SETS[activeSet].label + "|" + t; return problemIdByKey[k] ? problemIdByKey[k].id : null; }'
        + 'return null; })')(title);
    } catch (e) { return null; }
  }

  function enhanceProblem(frameEl, doc, win, problemEl) {
    if (problemEl.getAttribute('data-pt') === '1') return;
    var m = /^problem-(\d+)$/.exec(problemEl.id || '');
    if (!m) return;
    var idx = m[1];
    var ta = doc.getElementById('code-' + idx);
    var gutter = doc.getElementById('gutter-' + idx);
    var editor = ta && ta.parentElement;
    var submitRow = problemEl.querySelector('.submit-row');
    var submitBtn = doc.getElementById('submit-btn-' + idx);
    if (!ta || !gutter || !editor || !submitRow || !submitBtn) return;
    problemEl.setAttribute('data-pt', '1');

    /* ---- sənədin özündən məlumat: nömrə, başlıq, nümunə giriş/çıxış */
    var numEl = problemEl.querySelector('.problem-num');
    var titleEl = problemEl.querySelector('h3');
    var num = numEl ? numEl.textContent.trim() : idx;
    var title = titleEl ? titleEl.textContent.trim() : idx;
    var sampleIn = null, sampleOut = null;
    var boxes = problemEl.querySelectorAll('.problem-left .io-box');
    for (var b = 0; b < boxes.length; b++) {
      var lbl = boxes[b].querySelector('.lbl');
      var pre = boxes[b].querySelector('pre');
      if (!lbl || !pre) continue;
      var t = lbl.textContent.trim().toLowerCase();
      if (t.indexOf('nümunə giriş') === 0) sampleIn = pre.textContent;
      else if (t.indexOf('nümunə çıxış') === 0) sampleOut = pre.textContent;
    }

    /* ---- redaktor quruluşu: rəngləmə təbəqəsi + öz hadisələrimiz */
    var starter = ta.value;
    ta.removeAttribute('oninput');
    ta.removeAttribute('onscroll');
    ta.setAttribute('autocapitalize', 'off');
    ta.setAttribute('autocorrect', 'off');
    ta.setAttribute('autocomplete', 'off');
    ta.setAttribute('aria-describedby', 'pt-keys-' + idx);
    editor.classList.add('pt-editor');
    var hl = doc.createElement('pre');
    hl.className = 'pt-hl';
    hl.setAttribute('aria-hidden', 'true');
    editor.insertBefore(hl, editor.firstChild);

    var meta = doc.createElement('div');
    meta.className = 'pt-meta';
    meta.innerHTML = '<div class="pt-check" aria-live="polite"></div>'
      + '<div class="pt-hint"><span class="pt-draft"></span> <button type="button" class="pt-link pt-reset" hidden>Şablona qaytar</button></div>';
    editor.parentElement.insertBefore(meta, editor.nextSibling);
    var checkEl = meta.querySelector('.pt-check');
    var draftEl = meta.querySelector('.pt-draft');
    var resetBtn = meta.querySelector('.pt-reset');

    var keys = doc.createElement('div');
    keys.className = 'pt-meta pt-keys';
    keys.id = 'pt-keys-' + idx;
    keys.innerHTML = '<span>Tab = 4 boşluq · Esc, sonra Tab = redaktordan çıx</span><span>Ctrl+Enter = göndər · Ctrl+Shift+Enter = nümunə ilə sına</span>';
    meta.parentElement.insertBefore(keys, meta.nextSibling);

    /* ---- nümunə sınağı və öz giriş */
    var sampleBtn = doc.createElement('button');
    sampleBtn.type = 'button';
    sampleBtn.className = 'pt-btn';
    sampleBtn.textContent = '▶ Nümunə ilə sına';
    sampleBtn.title = 'Kodu nümunə giriş ilə işə salır və çıxışı gözlənilənlə müqayisə edir (Ctrl+Shift+Enter)';
    sampleBtn.disabled = sampleIn === null;
    var customBtn = doc.createElement('button');
    customBtn.type = 'button';
    customBtn.className = 'pt-btn ghost';
    customBtn.textContent = 'Öz giriş ilə…';
    customBtn.setAttribute('aria-expanded', 'false');
    submitBtn.insertAdjacentElement('afterend', customBtn);
    submitBtn.insertAdjacentElement('afterend', sampleBtn);

    var custom = doc.createElement('div');
    custom.className = 'pt-custom';
    custom.innerHTML = '<label class="pt-label" for="pt-stdin-' + idx + '">Öz girişin (stdin)</label>'
      + '<textarea class="pt-stdin" id="pt-stdin-' + idx + '" spellcheck="false" placeholder="Məs.: 17 5"></textarea>'
      + '<button type="button" class="pt-btn pt-run-custom">▶ İşə sal</button>';
    var run = doc.createElement('div');
    run.className = 'pt-run';
    run.setAttribute('aria-live', 'polite');
    submitRow.insertAdjacentElement('afterend', run);
    submitRow.insertAdjacentElement('afterend', custom);
    var stdinTa = custom.querySelector('.pt-stdin');
    var runCustomBtn = custom.querySelector('.pt-run-custom');
    var hintEl = doc.getElementById('hint-' + idx);

    /* ---- vəziyyət */
    var compilerResult = null; // { text, errors: {sətir: mesaj} }
    var liteErrors = {};
    var liteTimer = null, compileTimer = null, draftTimer = null;
    var inFlightCheck = false, lastCheckAt = 0, lastCheckedText = null;
    var key = null;
    var user = getUser();
    if (user && user.id !== undefined) key = draftKey(user.id, frameEl.id, num, title);

    function effectiveErrors(text, lineCount) {
      if (!text.trim()) return {};
      var src;
      if (compilerResult && compilerResult.text === text) src = compilerResult.errors;
      else src = Object.assign({}, compilerResult ? compilerResult.errors : {}, liteErrors);
      var o = {};
      Object.keys(src).forEach(function (k) { if (+k <= lineCount) o[k] = src[k]; });
      return o;
    }

    function render() {
      var text = ta.value;
      var lines = text.split('\n');
      var errs = effectiveErrors(text, lines.length);
      var html = highlightCppLines(text).map(function (h, i) {
        return errs[i + 1] ? '<span class="pt-err">' + (h || ' ') + '</span>' : h;
      }).join('\n');
      hl.innerHTML = html + '\n';
      var g = '';
      for (var n = 1; n <= lines.length; n++) g += (errs[n] ? '<span class="pt-gut-err">' + n + '</span>' : n) + '\n';
      gutter.innerHTML = g;
      renderCheckMsg(errs);
      syncScroll();
    }

    function renderCheckMsg(errs) {
      var ks = Object.keys(errs).map(Number).sort(function (a, b) { return a - b; });
      if (ks.length) {
        checkEl.className = 'pt-check bad';
        checkEl.innerHTML = ks.slice(0, 3).map(function (k) { return 'Sətir ' + k + ': ' + escapeHtml(errs[k]); }).join('<br>')
          + (ks.length > 3 ? '<br>… və daha ' + (ks.length - 3) + ' səhv' : '');
      } else if (compilerResult && compilerResult.text === ta.value && ta.value.trim()) {
        checkEl.className = 'pt-check ok';
        checkEl.textContent = '✓ Sintaksis səhvi tapılmadı';
      } else {
        checkEl.className = 'pt-check';
        checkEl.textContent = '';
      }
    }

    function syncScroll() {
      hl.scrollTop = ta.scrollTop;
      hl.scrollLeft = ta.scrollLeft;
      gutter.scrollTop = ta.scrollTop;
    }

    /* ---- qaralama */
    function saveDraftNow() {
      if (!key) return;
      try {
        if (ta.value === starter || !ta.value.trim()) localStorage.removeItem(key);
        else localStorage.setItem(key, JSON.stringify({ code: ta.value, t: Date.now() }));
        updateDraftNote(false);
      } catch (e) { /* məs. brauzer saxlama qadağası — sükutla keç */ }
    }
    function updateDraftNote(restored) {
      var changed = ta.value !== starter;
      resetBtn.hidden = !changed;
      if (restored) draftEl.textContent = 'Qaralama bərpa olundu.';
      else if (changed && key) draftEl.textContent = 'Qaralama yadda saxlanıldı.';
      else draftEl.textContent = '';
    }
    if (key) {
      try {
        var raw = localStorage.getItem(key);
        var saved = raw ? JSON.parse(raw) : null;
        if (saved && typeof saved.code === 'string' && saved.code !== starter) {
          ta.value = saved.code;
          updateDraftNote(true);
        }
      } catch (e) { /* zədəli qaralama — nəzərə alma */ }
    }
    resetBtn.addEventListener('click', async function () {
      if (!(await ask(win, 'Yazdığın kod silinəcək və redaktor başlanğıc şablonla əvəz olunacaq.',
        { title: 'Kod şablona qaytarılsın?', okText: 'Şablona qaytar', danger: true }))) return;
      ta.value = starter;
      compilerResult = null; liteErrors = {}; lastText = starter;
      saveDraftNow();
      render();
      ta.focus();
    });

    /* ---- səhv yoxlaması */
    function scheduleChecks() {
      clearTimeout(liteTimer);
      liteTimer = setTimeout(function () { liteErrors = liteCheckCpp(ta.value); render(); }, 400);
      clearTimeout(compileTimer);
      compileTimer = setTimeout(runCompilerCheck, 2500);
    }
    async function runCompilerCheck() {
      var text = ta.value;
      if (!text.trim() || !root.azcupApiFetch) return;
      if (compilerResult && compilerResult.text === text) return;
      if (text === lastCheckedText) return;
      if (inFlightCheck || doc.hidden) return;
      if (Object.keys(liteCheckCpp(text)).length) return; // bariz mötərizə səhvi var: serveri yükləməyə dəyməz
      var wait = 4000 - (Date.now() - lastCheckAt);
      if (wait > 0) { clearTimeout(compileTimer); compileTimer = setTimeout(runCompilerCheck, wait); return; }
      inFlightCheck = true; lastCheckAt = Date.now(); lastCheckedText = text;
      try {
        var res = await root.azcupApiFetch('/api/run/check', { method: 'POST', body: JSON.stringify({ sourceCode: text }) });
        if (res.ok) {
          var data = await res.json();
          // checked=false (server məşğul / g++ yoxdur) "səhv yoxdur" demək deyil
          if (data.checked) { compilerResult = { text: text, errors: mapCompilerErrors(text, data.errors) }; render(); }
        }
      } catch (e) { /* şəbəkə xətası — növbəti yazıdan sonra yenidən yoxlanacaq */ }
      finally { inFlightCheck = false; }
    }

    /* ---- redaktə: undo yığınını qorumaq üçün execCommand, alınmazsa setRangeText */
    function applyEdit(edit) {
      if (edit.skip) { ta.setSelectionRange(edit.selStart, edit.selEnd); return; }
      var before = ta.value;
      var expected = before.slice(0, edit.start) + edit.text + before.slice(edit.end);
      ta.setSelectionRange(edit.start, edit.end);
      var ok = false;
      try { ok = edit.text === '' ? doc.execCommand('delete') : doc.execCommand('insertText', false, edit.text); } catch (e) { ok = false; }
      if (!ok || ta.value !== expected) {
        ta.value = expected;
        ta.dispatchEvent(new win.Event('input', { bubbles: true }));
      }
      ta.setSelectionRange(edit.selStart, edit.selEnd);
    }

    var escPressed = false;
    ta.addEventListener('keydown', function (e) {
      if (e.isComposing) return;
      var mod = e.ctrlKey || e.metaKey;
      if (e.key === 'Escape') { escPressed = true; return; }
      var releasing = escPressed;
      escPressed = false;
      if (mod && e.key === 'Enter') {
        e.preventDefault();
        if (e.shiftKey) sampleBtn.click(); else submitBtn.click();
        return;
      }
      if (mod || e.altKey) return;
      if (e.key === 'Tab' && releasing) return; // Esc → Tab: fokus redaktordan çıxır
      var edit = editForKey(ta.value, ta.selectionStart, ta.selectionEnd, e.key, { shift: e.shiftKey });
      if (!edit) return;
      e.preventDefault();
      applyEdit(edit);
    });
    ta.addEventListener('blur', function () { escPressed = false; clearTimeout(draftTimer); saveDraftNow(); });
    ta.addEventListener('scroll', syncScroll);
    var lastText = ta.value;
    ta.addEventListener('input', function () {
      if (compilerResult) compilerResult = { text: compilerResult.text, errors: reconcileErrors(lastText, ta.value, compilerResult.errors) };
      lastText = ta.value;
      render();
      clearTimeout(draftTimer);
      draftTimer = setTimeout(saveDraftNow, 600);
      scheduleChecks();
    });

    /* ---- göndərmədən sonra kompilyasiya xətası: sətirləri işarələ (səhifənin öz kodu STDERR-i yazır) */
    var verdictEl = doc.getElementById('verdict-' + idx);
    var badgeEl = doc.getElementById('badge-' + idx);
    var stderrEl = doc.getElementById('stderr-' + idx);
    if (verdictEl && badgeEl && stderrEl) {
      new win.MutationObserver(function () {
        if (!/\bCOMPILE_ERROR\b/.test(badgeEl.className)) return;
        var list = parseCompileErrors(stderrEl.textContent);
        if (!list.length) return;
        compilerResult = { text: ta.value, errors: mapCompilerErrors(ta.value, list) };
        render();
      }).observe(verdictEl, { childList: true, subtree: true, characterData: true, attributes: true, attributeFilter: ['class'] });
    }

    /* ---- işə salma (nümunə və öz giriş) */
    customBtn.addEventListener('click', function () {
      var open = !custom.classList.contains('show');
      custom.classList.toggle('show', open);
      customBtn.setAttribute('aria-expanded', open ? 'true' : 'false');
      if (open) stdinTa.focus();
    });

    function setHint(msg) { if (hintEl) hintEl.textContent = msg || ''; }

    async function runCode(kind) {
      var sourceCode = ta.value;
      if (!sourceCode.trim()) { setHint('Kod boşdur.'); return; }
      if (!root.azcupApiFetch || !getUser()) { setHint('Davam etmək üçün yenidən daxil ol.'); return; }
      var stdin = kind === 'sample' ? (sampleIn || '') : stdinTa.value;
      var btn = kind === 'sample' ? sampleBtn : runCustomBtn;
      btn.disabled = true;
      setHint('Kompilyasiya edilir və icra olunur...');
      try {
        var res = await root.azcupApiFetch('/api/run', { method: 'POST', body: JSON.stringify({ sourceCode: sourceCode, stdin: stdin }) });
        var data = await res.json();
        if (!res.ok) { setHint(data.message || 'İcra uğursuz oldu.'); return; }
        setHint('');
        showRunResult(kind, data);
        if (data.status === 'COMPILE_ERROR') {
          var list = parseCompileErrors(data.stderr);
          if (list.length) { compilerResult = { text: sourceCode, errors: mapCompilerErrors(sourceCode, list) }; render(); }
        }
      } catch (e) {
        setHint('Serverə qoşulmaq mümkün olmadı.');
      } finally {
        btn.disabled = kind === 'sample' && sampleIn === null;
      }
    }

    function showRunResult(kind, data) {
      var cls = data.status, label = RUN_LABELS[data.status] || data.status, cmp = null;
      if (kind === 'sample' && data.status === 'ACCEPTED' && sampleOut !== null) {
        cmp = compareOutputs(sampleOut, data.stdout);
        if (cmp.equal) label = '✓ NÜMUNƏ KEÇDİ';
        else { cls = 'WRONG_ANSWER'; label = '✗ NÜMUNƏ UYĞUN GƏLMİR · ' + cmp.line + '-ci sətir'; }
      }
      var time = typeof data.executionTimeMs === 'number' ? '<span class="pt-run-time">' + data.executionTimeMs + ' ms</span>' : '';
      var body;
      if (cmp && !cmp.equal) {
        body = '<div class="io-grid">'
          + '<div class="io-box"><div class="lbl">Gözlənilən</div><pre>' + diffLines(cmp.expected, cmp.line) + '</pre></div>'
          + '<div class="io-box"><div class="lbl">Sənin çıxışın</div><pre>' + diffLines(cmp.actual, cmp.line) + '</pre></div>'
          + '</div>';
      } else {
        body = '<div class="io-grid">'
          + '<div class="io-box"><div class="lbl">STDOUT</div><pre>' + escapeHtml(clip(data.stdout)) + '</pre></div>'
          + '<div class="io-box"><div class="lbl">STDERR</div><pre>' + escapeHtml(clip(data.stderr)) + '</pre></div>'
          + '</div>';
      }
      run.innerHTML = '<span class="verdict-badge ' + cls + '">' + escapeHtml(label) + '</span>' + time + body;
      run.classList.add('show');
    }

    sampleBtn.addEventListener('click', function () { if (!sampleBtn.disabled) runCode('sample'); });
    runCustomBtn.addEventListener('click', function () { runCode('custom'); });

    /* ---- 5) mərhələli köməklər: teqlər → yanaşma (müəllim/admin üçün hamısı açıq qalır) */
    var left = problemEl.querySelector('.problem-left');
    var steps = [];
    var tagRow = left && left.querySelector('.tag-row');
    var approachBlock = left && left.querySelector('.approach-block');
    var revealBtnEl = left && left.querySelector('.reveal-btn'); // "Həlli göstər (GitHub)" — sonuncu addım
    if (tagRow) steps.push({ el: tagRow, label: '💡 İpucu göstər (mövzu teqləri)' });
    if (approachBlock) steps.push({ el: approachBlock, label: '💡 Yanaşmanı göstər' });
    var role = user && user.role;
    if (steps.length && role !== 'TEACHER' && role !== 'ADMIN') {
      var hintStoreKey = user && user.id !== undefined ? hintKey(user.id, frameEl.id, num, title) : null;
      var hintBox = doc.createElement('div');
      hintBox.className = 'pt-hints';
      hintBox.innerHTML = '<button type="button" class="pt-btn ghost pt-hint-btn"></button>'
        + '<span class="pt-hint-note">Əvvəlcə özün cəhd et. Köməklər addım-addım açılır: ipucu → yanaşma → həll.</span>';
      left.insertBefore(hintBox, steps[0].el);
      var hintBtn = hintBox.querySelector('.pt-hint-btn');
      var level = 0;
      var setLevel = function (n, persist) {
        level = Math.max(0, Math.min(steps.length, n));
        for (var s = 0; s < steps.length; s++) steps[s].el.classList.toggle('pt-hide', s >= level);
        if (revealBtnEl) revealBtnEl.classList.toggle('pt-hide', level < steps.length);
        if (level >= steps.length) hintBox.classList.add('pt-hide');
        else { hintBox.classList.remove('pt-hide'); hintBtn.textContent = steps[level].label; }
        if (persist && hintStoreKey && level > 0) { try { localStorage.setItem(hintStoreKey, String(level)); } catch (e) { /* saxlama qadağası */ } }
      };
      var savedLevel = 0;
      if (hintStoreKey) { try { savedLevel = parseInt(localStorage.getItem(hintStoreKey), 10) || 0; } catch (e) { savedLevel = 0; } }
      var solvedEl = doc.getElementById('solved-' + idx);
      var solvedNow = function () { return !!solvedEl && solvedEl.style.display !== 'none'; };
      setLevel(solvedNow() ? steps.length : savedLevel, false);
      hintBtn.addEventListener('click', function () { setLevel(level + 1, true); });
      // Məsələ həll edilibsə (səhifə nişanı göstərəndə) bütün köməklər açılır.
      if (solvedEl) {
        new win.MutationObserver(function () { if (solvedNow() && level < steps.length) setLevel(steps.length, true); })
          .observe(solvedEl, { attributes: true, attributeFilter: ['style'] });
      }
    }

    /* ---- 6) tarixçə: hər göndərişin kodunu göstər və redaktora yüklə (API kodu artıq qaytarır) */
    var historyBtn = submitRow.querySelector('.history-btn');
    var historyEl = doc.getElementById('history-' + idx);
    var histList = [];
    var histTimer = null;
    var SUBMIT_LABELS = {
      ACCEPTED: '✓ QƏBUL EDİLDİ',
      WRONG_ANSWER: '✗ SƏHV CAVAB',
      COMPILE_ERROR: '⚠ KOMPİLYASİYA XƏTASI',
      RUNTIME_ERROR: '⚠ İCRA XƏTASI',
      TIME_LIMIT_EXCEEDED: '⏱ VAXT LİMİTİ AŞILDI',
    };
    function fmtDate(iso) {
      try { return new Date(iso).toLocaleString('az-AZ', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }); }
      catch (e) { return ''; }
    }
    function renderHistory(showAll) {
      if (!histList.length) { historyEl.innerHTML = '<p class="history-empty">Hələ göndəriş yoxdur.</p>'; return; }
      var LIMIT = 15;
      var rows = showAll ? histList : histList.slice(0, LIMIT);
      historyEl.innerHTML = rows.map(function (s, i) {
        var tests = s.totalTestCases > 1 ? s.passedTestCases + '/' + s.totalTestCases + ' test · ' : '';
        return '<div class="pt-hist-row" data-i="' + i + '"><div class="history-item">'
          + '<span class="verdict-badge ' + escapeHtml(s.status) + '">' + escapeHtml(SUBMIT_LABELS[s.status] || s.status) + '</span>'
          + '<span class="history-time">' + tests + escapeHtml(fmtDate(s.submittedAt)) + '</span></div>'
          + '<div class="pt-hist-actions"><button type="button" class="pt-link pt-hist-view" aria-expanded="false">Kodu göstər</button>'
          + '<button type="button" class="pt-link pt-hist-load">Redaktora yüklə</button></div>'
          + '<pre class="pt-hist-code"></pre></div>';
      }).join('') + (!showAll && histList.length > LIMIT
        ? '<button type="button" class="pt-link pt-hist-more">Daha köhnə ' + (histList.length - LIMIT) + ' göndərişi göstər</button>' : '');
    }
    async function loadHistory() {
      var pid = lookupProblemId(win, title);
      if (!pid) { historyEl.innerHTML = '<p class="history-empty">Məsələ məlumatı hələ yüklənməyib.</p>'; return; }
      if (!histList.length) historyEl.innerHTML = '<p class="history-empty">Yüklənir...</p>';
      try {
        var res = await root.azcupApiFetch('/api/problems/' + pid + '/submissions');
        var data = await res.json();
        if (!res.ok) { historyEl.innerHTML = '<p class="history-empty">' + escapeHtml(data.message || 'Yüklənə bilmədi.') + '</p>'; return; }
        histList = data;
        renderHistory(false);
      } catch (e) {
        historyEl.innerHTML = '<p class="history-empty">Yüklənə bilmədi.</p>';
      }
    }
    async function loadIntoEditor(code) {
      if (ta.value !== code && ta.value.trim() && ta.value !== starter
          && !(await ask(win, 'Redaktordakı kod bu göndərişin kodu ilə əvəz olunacaq. Davam edək?',
            { title: 'Kod əvəz olunsun?', okText: 'Əvəz et', danger: false }))) return;
      ta.value = code;
      ta.dispatchEvent(new win.Event('input', { bubbles: true }));
      ta.focus();
      editor.scrollIntoView({ block: 'nearest' });
    }
    if (historyBtn && historyEl) {
      historyBtn.removeAttribute('onclick'); // səhifənin köhnə (kodsuz) tarixçəsi əvəzinə bu işləyir
      historyBtn.setAttribute('aria-expanded', 'false');
      historyBtn.addEventListener('click', function () {
        var open = !historyEl.classList.contains('show');
        historyEl.classList.toggle('show', open);
        historyBtn.setAttribute('aria-expanded', open ? 'true' : 'false');
        if (open) loadHistory();
      });
      historyEl.addEventListener('click', function (e) {
        var t = e.target;
        if (!t || !t.closest) return;
        var row = t.closest('.pt-hist-row');
        if (t.closest('.pt-hist-more')) { renderHistory(true); return; }
        if (!row) return;
        var item = histList[parseInt(row.getAttribute('data-i'), 10)];
        if (!item) return;
        if (t.closest('.pt-hist-view')) {
          var pre = row.querySelector('.pt-hist-code');
          var btn = row.querySelector('.pt-hist-view');
          var show = !pre.classList.contains('show');
          if (show && !pre.innerHTML) pre.innerHTML = highlightCppLines(item.sourceCode || '').join('\n');
          pre.classList.toggle('show', show);
          btn.setAttribute('aria-expanded', show ? 'true' : 'false');
          btn.textContent = show ? 'Kodu gizlət' : 'Kodu göstər';
        } else if (t.closest('.pt-hist-load')) {
          loadIntoEditor(item.sourceCode || '');
        }
      });
      // Yeni göndəriş nəticəsi gələndə açıq tarixçə yenilənir.
      if (badgeEl) {
        new win.MutationObserver(function () {
          if (!historyEl.classList.contains('show')) return;
          clearTimeout(histTimer);
          histTimer = setTimeout(loadHistory, 400);
        }).observe(badgeEl, { childList: true, characterData: true, subtree: true });
      }
    }

    render();
    updateDraftNote(false);
    if (ta.value !== starter && key) draftEl.textContent = 'Qaralama bərpa olundu.';
  }

  function clip(s) {
    s = String(s || '');
    return s.length > 4000 ? s.slice(0, 4000) + '\n… (çıxış qısaldıldı)' : s;
  }

  function diffLines(lines, badLine) {
    return lines.slice(0, 200).map(function (l, i) {
      var h = escapeHtml(l) || ' ';
      return i + 1 === badLine ? '<mark class="pt-diff">' + h + '</mark>' : h;
    }).join('\n');
  }

  var api = {
    attachFrame: attachFrame,
    editForKey: editForKey,
    highlightCppLines: highlightCppLines,
    liteCheckCpp: liteCheckCpp,
    parseCompileErrors: parseCompileErrors,
    mapCompilerErrors: mapCompilerErrors,
    reconcileErrors: reconcileErrors,
    normalizeOutput: normalizeOutput,
    compareOutputs: compareOutputs,
    draftKey: draftKey,
    hintKey: hintKey,
    foldAz: foldAz,
    matchQuery: matchQuery,
    escapeHtml: escapeHtml,
  };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.AzcupProblemTools = api;
})(typeof window !== 'undefined' ? window : globalThis);
