// Pure helpers for search, export, operations tasks, and the labelled
// deterministic AI fallback. No provider call or database access happens here.

export function fallbackReview(prompt, feature = 'cloud-account-review') {
  const text = String(prompt || '').slice(0, 500);
  const content = [
    'Deterministic fallback (no AI provider configured).',
    '',
    `Feature: ${feature}`,
    `Request reviewed locally: ${text || '(empty)'}`,
    'Checklist: confirm registration details, complete email verification, accept terms, sign in, and keep activation behind an explicit human control.',
    'No external provider was called and no account state was changed by this fallback.',
  ].join('\n');
  return { content, model: 'deterministic-fallback', fallback: true, aiGenerated: false, providerReceipt: { provider: 'deterministic-fallback' } };
}

function rowText(row) {
  return [JSON.stringify(row?.input ?? ''), row?.output?.content, row?.feature, row?.model].filter(Boolean).join('\n').toLowerCase();
}

export function searchHistory(rows, query) {
  const q = String(query || '').trim().toLowerCase();
  if (!q) return [...rows];
  return rows.filter((row) => rowText(row).includes(q));
}

const csvCell = (v) => `"${String(v ?? '').replaceAll('"', '""')}"`;

export function exportHistory(rows, format = 'json') {
  if (format === 'csv') {
    const lines = ['id,feature,model,createdAt,content'];
    for (const r of rows) lines.push([r.id, r.feature ?? '', r.model ?? '', r.createdAt ?? '', r.output?.content ?? ''].map(csvCell).join(','));
    return { contentType: 'text/csv; charset=utf-8', body: lines.join('\n') + '\n' };
  }
  if (format === 'txt' || format === 'text') {
    const body = rows.map((r) => `# Report ${r.id} (${r.feature ?? ''} / ${r.model ?? ''})\n\n${r.output?.content ?? ''}`).join('\n\n---\n\n');
    return { contentType: 'text/plain; charset=utf-8', body: body + '\n' };
  }
  return { contentType: 'application/json', body: JSON.stringify(rows, null, 2) + '\n' };
}

export function createTaskStore() {
  const byUser = new Map();
  let nextId = 1;
  const list = (userId) => [...(byUser.get(userId) || [])];
  return {
    list,
    create(userId, title) {
      const clean = String(title || '').trim();
      if (!clean || clean.length > 200) throw Object.assign(new Error('Task title must contain 1-200 characters'), { status: 422 });
      const task = { id: nextId++, title: clean, done: false, createdAt: new Date().toISOString() };
      byUser.set(userId, [...list(userId), task]);
      return task;
    },
    update(userId, id, patch = {}) {
      const task = list(userId).find((t) => t.id === Number(id));
      if (!task) return null;
      if (typeof patch.title === 'string') {
        const clean = patch.title.trim();
        if (!clean || clean.length > 200) throw Object.assign(new Error('Task title must contain 1-200 characters'), { status: 422 });
        task.title = clean;
      }
      if (typeof patch.done === 'boolean') task.done = patch.done;
      return task;
    },
    remove(userId, id) {
      const tasks = list(userId);
      const next = tasks.filter((t) => t.id !== Number(id));
      if (next.length === tasks.length) return false;
      byUser.set(userId, next);
      return true;
    },
  };
}

export function cloudAccountReview(rows = []) {
  const pending = rows.filter((r) => /verif|pending|activate/i.test(rowText(r))).length;
  return { aiGenerated: false, fallback: true, feature: 'cloud-account-review', summary: `${rows.length} saved record(s); ${pending} mention verification/activation.`, pendingSignals: pending };
}
export function seatUsageSummary(rows = []) {
  const text = rows.map((r) => rowText(r)).join(' ');
  const seats = [...text.matchAll(/(\d+)\s*seats?/gi)].map((m) => Number(m[1]));
  return { aiGenerated: false, fallback: true, feature: 'seat-usage-summary', summary: seats.length ? `Seat counts mentioned: ${seats.join(', ')}.` : 'No seat counts in saved records.', seats };
}
export function billingRiskCheck(rows = []) {
  const hits = rows.filter((r) => /billing|payment|invoice|overdue/i.test(rowText(r))).length;
  return { aiGenerated: false, fallback: true, feature: 'billing-risk-check', summary: hits ? `${hits} record(s) mention billing/payment risk.` : 'No billing risk signals in saved records.', riskSignals: hits };
}
