import test from 'node:test';
import assert from 'node:assert/strict';
import { createTaskStore, exportHistory, fallbackReview, searchHistory, cloudAccountReview, seatUsageSummary, billingRiskCheck } from './features.mjs';

const rows = [
  { id: 1, feature: 'cloud-account-review', input: { prompt: 'verify account' }, output: { content: 'Check verification email' }, model: 'm1', createdAt: '2026-01-01' },
  { id: 2, feature: 'cloud-account-review', input: { prompt: 'billing' }, output: { content: 'Review seats' }, model: 'm2', createdAt: '2026-01-02' },
];

test('fallback is labelled, deterministic, and needs no provider', () => {
  const a = fallbackReview('verify account');
  assert.equal(a.fallback, true);
  assert.match(a.content, /Deterministic fallback/);
  assert.deepEqual(a, fallbackReview('verify account'));
});

test('search filters saved history', () => {
  assert.equal(searchHistory(rows, 'billing').length, 1);
  assert.equal(searchHistory(rows, '').length, 2);
});

test('export renders json, csv, and text', () => {
  assert.match(exportHistory(rows, 'json').body, /verify account/);
  assert.match(exportHistory(rows, 'csv').body, /^id,feature,model,createdAt,content/);
  assert.match(exportHistory(rows, 'txt').body, /# Report 1/);
});

test('task store validates and isolates users', () => {
  const store = createTaskStore();
  const task = store.create('u1', 'Verify account');
  assert.equal(store.list('u1').length, 1);
  assert.equal(store.list('u2').length, 0);
  assert.equal(store.update('u1', task.id, { done: true }).done, true);
  assert.throws(() => store.create('u1', '  '), /1-200/);
  assert.equal(store.remove('u1', task.id), true);
  assert.equal(store.remove('u1', task.id), false);
});

test('domain AI features are deterministic with aiGenerated:false', () => {
  for (const f of [cloudAccountReview(rows), seatUsageSummary(rows), billingRiskCheck(rows)]) assert.equal(f.aiGenerated, false);
  assert.equal(fallbackReview('verify account').aiGenerated, false);
  assert.equal(cloudAccountReview(rows).pendingSignals, 1);
});
