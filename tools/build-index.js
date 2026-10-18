// Rebuilds index.html's embedded base64 page constants from the editable
// sources in frontend-src/. Run after editing any frontend-src/* file.
// Everything else in index.html (shell markup/auth JS) is left untouched, except
// the generated blocks between the *:BEGIN / *:END markers (see below).
'use strict';
const fs = require('fs');
const path = require('path');

const ROOT = path.resolve(__dirname, '..');
const INDEX_HTML = path.join(ROOT, 'index.html');
const SRC_DIR = path.join(ROOT, 'frontend-src');
const STATIC_COPY = path.join(ROOT, 'backend', 'src', 'main', 'resources', 'static', 'index.html');

const FILES = {
  PREP: 'prep.html',
  WEEK1: 'week1.html',
  WEEK2: 'week2.html',
  WEEK3: 'week3.html',
  WEEK4: 'week4.html',
  WEEK5: 'week5.html',
  ALGO: 'algo.html',
  SCHEME: 'scheme.html',
  PYTHON: 'python.html',
};

function readSrc(name) {
  return fs.readFileSync(path.join(SRC_DIR, name), 'utf-8').replace(/\r\n/g, '\n').replace(/\n+$/, '');
}

// Ortaq dizayn qatı (ui-polish.css) və interfeys alətləri (ui-kit.js) HƏR iframe səhifəsinə də,
// index.html qabığına da əlavə olunur. Mənbə səhifələrinə (frontend-src/*.html) toxunulmur.
const polishCss = readSrc('ui-polish.css');
const shellCss = readSrc('ui-shell.css');
const kitJs = readSrc('ui-kit.js');
if (/<\/style/i.test(polishCss + shellCss)) throw new Error('CSS must not contain "</style"');
if (/<\/script/i.test(kitJs)) throw new Error('ui-kit.js must not contain "</script"');

// Python bölməsinin dərsləri frontend-src/python-lessons/*.js fayllarındadır (ad sırası ilə birləşdirilir) və python.html-dəki
// "/* PY_LESSONS */" yerinə yazılır. Hər fayl PY_CHAPTERS.push({...}) çağırır.
const PY_LESSONS_MARKER = '/* PY_LESSONS */';
function readPythonLessons() {
  const dir = path.join(SRC_DIR, 'python-lessons');
  const files = fs.readdirSync(dir).filter(f => f.endsWith('.js')).sort();
  if (!files.length) throw new Error('frontend-src/python-lessons/ içində dərs faylı yoxdur');
  const js = files.map(f => fs.readFileSync(path.join(dir, f), 'utf-8').replace(/\r\n/g, '\n').replace(/\n+$/, '')).join('\n\n');
  if (/<\/script/i.test(js)) throw new Error('python-lessons must not contain "</script" (inlined into a <script> block)');
  console.log('PY_LESSONS <-', files.length, 'files (' + js.length + ' chars)');
  return js;
}
function injectPythonLessons(html) {
  if (!html.includes(PY_LESSONS_MARKER)) throw new Error('python.html-də ' + PY_LESSONS_MARKER + ' yeri tapılmadı');
  return html.replace(PY_LESSONS_MARKER, () => readPythonLessons());
}

function injectIntoPage(html, fileName) {
  const block = '<style id="azcup-ui-polish">\n' + polishCss + '\n</style>\n<script id="azcup-ui-kit">\n' + kitJs + '\n</script>\n';
  if (!/<\/head>/i.test(html)) throw new Error('No </head> found in ' + fileName + ' to inject the UI layer into');
  // Funksiya ilə əvəz: kitin içindəki "$&" kimi ardıcıllıqlar xüsusi mənada işlənməsin
  return html.replace(/<\/head>/i, () => block + '</head>');
}

let indexHtml = fs.readFileSync(INDEX_HTML, 'utf-8');
// Windows-da index.html CRLF ilədir: blokları LF ilə əvəz edib sonda eyni sətir sonunu bərpa edirik
const indexHadCRLF = indexHtml.includes('\r\n');
indexHtml = indexHtml.replace(/\r\n/g, '\n');

for (const [constName, fileName] of Object.entries(FILES)) {
  const srcPath = path.join(SRC_DIR, fileName);
  let source = fs.readFileSync(srcPath, 'utf-8');
  if (fileName === 'python.html') source = injectPythonLessons(source);
  const html = injectIntoPage(source, fileName);
  const b64 = Buffer.from(html, 'utf-8').toString('base64');
  const re = new RegExp('const ' + constName + '_HTML_B64 = "[A-Za-z0-9+/=]+";');
  if (!re.test(indexHtml)) {
    throw new Error('Could not find const ' + constName + '_HTML_B64 in index.html to replace');
  }
  indexHtml = indexHtml.replace(re, 'const ' + constName + '_HTML_B64 = "' + b64 + '";');
  console.log(constName, '<-', fileName, '(' + html.length + ' chars)');
}

// index.html-də "<open> BEGIN ... <close> END" işarələri arasındakı hissə mənbə fayldan yenidən yazılır.
function replaceBlock(name, content, source) {
  const open = '/* ' + name + ':BEGIN';
  const re = new RegExp('\\/\\* ' + name + ':BEGIN[^\\n]*\\*\\/[\\s\\S]*?\\/\\* ' + name + ':END \\*\\/');
  if (!re.test(indexHtml)) throw new Error('Could not find the ' + name + ':BEGIN/END markers in index.html');
  indexHtml = indexHtml.replace(re, () =>
    open + ' (' + source + '-dən tools/build-index.js ilə yaradılır — burada əl ilə dəyişməyin) */\n'
    + content + '\n/* ' + name + ':END */');
  console.log(name, '<-', source, '(' + content.length + ' chars)');
}

// frontend-src/problem-tools.js (məsələ həlli redaktoru, qaralama, nümunə sınağı)
const toolsJs = readSrc('problem-tools.js');
if (/<\/script/i.test(toolsJs)) {
  throw new Error('problem-tools.js must not contain "</script" (it is inlined into a <script> block)');
}
replaceBlock('PROBLEM_TOOLS', toolsJs, 'problem-tools.js');
replaceBlock('UI_KIT', kitJs, 'ui-kit.js');
replaceBlock('UI_POLISH', polishCss, 'ui-polish.css');
replaceBlock('UI_SHELL', shellCss, 'ui-shell.css');

if (indexHadCRLF) indexHtml = indexHtml.replace(/\n/g, '\r\n');
fs.writeFileSync(INDEX_HTML, indexHtml, 'utf-8');
console.log('Wrote', INDEX_HTML);

fs.mkdirSync(path.dirname(STATIC_COPY), { recursive: true });
fs.copyFileSync(INDEX_HTML, STATIC_COPY);
console.log('Copied to', STATIC_COPY);
