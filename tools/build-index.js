// Rebuilds index.html's embedded base64 page constants from the editable
// sources in frontend-src/. Run after editing any frontend-src/*.html file.
// Everything else in index.html (shell/CSS/tabbar/auth JS) is left untouched.
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

let indexHtml = fs.readFileSync(INDEX_HTML, 'utf-8');

for (const [constName, fileName] of Object.entries(FILES)) {
  const srcPath = path.join(SRC_DIR, fileName);
  const html = fs.readFileSync(srcPath, 'utf-8');
  const b64 = Buffer.from(html, 'utf-8').toString('base64');
  const re = new RegExp('const ' + constName + '_HTML_B64 = "[A-Za-z0-9+/=]+";');
  if (!re.test(indexHtml)) {
    throw new Error('Could not find const ' + constName + '_HTML_B64 in index.html to replace');
  }
  indexHtml = indexHtml.replace(re, 'const ' + constName + '_HTML_B64 = "' + b64 + '";');
  console.log(constName, '<-', fileName, '(' + html.length + ' chars)');
}

fs.writeFileSync(INDEX_HTML, indexHtml, 'utf-8');
console.log('Wrote', INDEX_HTML);

fs.mkdirSync(path.dirname(STATIC_COPY), { recursive: true });
fs.copyFileSync(INDEX_HTML, STATIC_COPY);
console.log('Copied to', STATIC_COPY);
