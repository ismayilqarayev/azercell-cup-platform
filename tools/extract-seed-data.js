// One-off script: pulls the problem data that's baked as base64 HTML into
// index.html and turns it into backend/src/main/resources/seed/seed-data.json.
// Not part of the running app — run manually whenever index.html's embedded
// content changes and the DB seed needs regenerating.
'use strict';
const fs = require('fs');
const path = require('path');
const vm = require('vm');

const ROOT = path.resolve(__dirname, '..');
const INDEX_HTML = path.join(ROOT, 'index.html');
const OUT_PATH = path.join(ROOT, 'backend', 'src', 'main', 'resources', 'seed', 'seed-data.json');

const indexHtml = fs.readFileSync(INDEX_HTML, 'utf-8');

function decodeB64Const(name) {
  const re = new RegExp('const ' + name + '_HTML_B64 = "([A-Za-z0-9+/=]+)";');
  const m = indexHtml.match(re);
  if (!m) throw new Error('Could not find ' + name + '_HTML_B64 in index.html');
  return Buffer.from(m[1], 'base64').toString('utf-8');
}

// Extracts the JS array literal starting at the `[` right after `const <name> =`.
// Hand-rolled instead of a regex because entries contain template-literal C++
// source (backticks, quotes, escaped newlines) that a naive regex would mis-match.
function extractArrayLiteral(text, constName) {
  const declRe = new RegExp('const\\s+' + constName + '\\s*=\\s*\\[');
  const declMatch = text.match(declRe);
  if (!declMatch) return null;
  const start = declMatch.index + declMatch[0].length - 1; // index of the opening [
  let i = start;
  let depth = 0;
  let mode = null; // null | 'single' | 'double' | 'template'
  for (; i < text.length; i++) {
    const c = text[i];
    const prev = text[i - 1];
    if (mode) {
      if (c === '\\' ) { i++; continue; } // skip escaped char
      if (mode === 'single' && c === "'") mode = null;
      else if (mode === 'double' && c === '"') mode = null;
      else if (mode === 'template' && c === '`') mode = null;
      continue;
    }
    if (c === "'") { mode = 'single'; continue; }
    if (c === '"') { mode = 'double'; continue; }
    if (c === '`') { mode = 'template'; continue; }
    if (c === '[' || c === '{') depth++;
    else if (c === ']' || c === '}') {
      depth--;
      if (depth === 0) { i++; break; }
    }
  }
  return text.slice(start, i);
}

function evalArrayLiteral(literalText) {
  return vm.runInNewContext('(' + literalText + ')', {}, { timeout: 5000 });
}

// ---- Topics (16-entry roadmap from PREP.html) ----
const prepHtml = decodeB64Const('PREP');
const weeksLiteral = extractArrayLiteral(prepHtml, 'weeks');
if (!weeksLiteral) throw new Error('Could not find `weeks` array in PREP.html');
const weeks = evalArrayLiteral(weeksLiteral);

const topics = weeks.map((w, idx) => ({
  slug: 'week' + (idx + 1),
  orderIndex: idx + 1,
  title: w.title,
  monthTag: w.tag || null,
  description: w.desc || null,
  published: idx < 5, // only week1-5 have problem content today
}));

// ---- Problems (week1 has 4 subgroups, week2-5 are flat `problems` arrays) ----
const WEEK_FILES = ['WEEK1', 'WEEK2', 'WEEK3', 'WEEK4', 'WEEK5'];
const WEEK1_SUBGROUPS = [
  { constName: 'mainProblems', label: 'Əsas Məsələlər' },
  { constName: 'forProblems', label: '"For" Dövrü Məsələləri' },
  { constName: 'ifswProblems', label: 'If/Switch Məsələləri' },
  { constName: 'vartypeProblems', label: 'Dəyişən/Tip/Operator Məsələləri' },
];

function toProblemRecord(p, topicSlug, orderIndex, subgroupLabel) {
  return {
    topicSlug,
    orderIndex,
    subgroupLabel: subgroupLabel || null,
    title: p.title,
    difficulty: p.difficulty || 'easy',
    tags: p.tags || [],
    statement: p.statement,
    inputSpec: p.input,
    outputSpec: p.output,
    exampleInput: p.example ? p.example.in : '',
    exampleOutput: p.example ? p.example.out : '',
    approach: p.approach || null,
    referenceSolution: p.solution || null,
  };
}

const problems = [];

WEEK_FILES.forEach((fileName, weekIdx) => {
  const topicSlug = 'week' + (weekIdx + 1);
  const html = decodeB64Const(fileName);
  let orderIndex = 1;

  if (fileName === 'WEEK1') {
    WEEK1_SUBGROUPS.forEach((group) => {
      const literal = extractArrayLiteral(html, group.constName);
      if (!literal) throw new Error('Missing ' + group.constName + ' in ' + fileName);
      const arr = evalArrayLiteral(literal);
      arr.forEach((p) => {
        problems.push(toProblemRecord(p, topicSlug, orderIndex++, group.label));
      });
    });
  } else {
    const literal = extractArrayLiteral(html, 'problems');
    if (!literal) throw new Error('Missing `problems` array in ' + fileName);
    const arr = evalArrayLiteral(literal);
    arr.forEach((p) => {
      problems.push(toProblemRecord(p, topicSlug, orderIndex++, null));
    });
  }

  console.log(fileName + ': ' + (orderIndex - 1) + ' problems extracted');
});

fs.mkdirSync(path.dirname(OUT_PATH), { recursive: true });
fs.writeFileSync(OUT_PATH, JSON.stringify({ topics, problems }, null, 2), 'utf-8');
console.log('Wrote ' + problems.length + ' problems across ' + topics.length + ' topics to ' + OUT_PATH);
