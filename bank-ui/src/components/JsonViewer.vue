<script setup lang="js">
import { computed, watch, ref, defineComponent, h } from 'vue';

/**
 * Props:
 *  - value: object to render as JSON
 *  - collapsible: enable expand/collapse on nested nodes
 *  - initiallyCollapsed: collapse all nested nodes initially
 *  - sortKeys: sort object keys alphabetically
 *  - indent: spaces used for indentation
 *  - showCopy: show "Copy JSON" button
 *  - quoteKeys: wrap object keys in quotes (valid JSON style)
 */
const props = defineProps({
  value: { type: null, required: true },
  collapsible: { type: Boolean, default: true },
  initiallyCollapsed: { type: Boolean, default: false },
  sortKeys: { type: Boolean, default: false },
  indent: { type: Number, default: 2 },
  showCopy: { type: Boolean, default: true },
  quoteKeys: { type: Boolean, default: false }
});

/** --- Utilities --- */
function isObject(v) {
  return v && typeof v === 'object' && !Array.isArray(v);
}
function isArray(v) {
  return Array.isArray(v);
}
function getKeys(o, sort) {
  const keys = Object.keys(o);
  return sort ? keys.sort((a, b) => a.localeCompare(b)) : keys;
}

/** --- Collapse state (WeakMap + version bump) --- */
const collapseState = new WeakMap();
/** bump this to force re-render after toggle/init */
const version = ref(0);

function initCollapse(o) {
  if (isObject(o) || isArray(o)) {
    if (!collapseState.has(o)) {
      collapseState.set(o, !!props.initiallyCollapsed);
    }
    const children = isArray(o) ? o : Object.values(o);
    children.forEach(initCollapse);
  }
}

watch(
  () => props.value,
  (val) => {
    initCollapse(val);
    version.value++; // trigger update
  },
  { immediate: true }
);

function toggle(node) {
  if (!props.collapsible) return;
  const current = collapseState.get(node) ?? !!props.initiallyCollapsed;
  collapseState.set(node, !current);
  version.value++; // trigger update
}

function isCollapsed(node) {
  const v = collapseState.get(node);
  return typeof v === 'boolean' ? v : !!props.initiallyCollapsed;
}

/** --- Safe stringify for copy --- */
function safeStringify(obj, space = 2) {
  const seen = new WeakSet();
  return JSON.stringify(
    obj,
    function (key, value) {
      if (typeof value === 'object' && value !== null) {
        if (seen.has(value)) return '[Circular]';
        seen.add(value);
      }
      return value;
    },
    space
  );
}

const jsonText = computed(() => safeStringify(props.value, props.indent));
const copied = ref(false);

async function copyJson() {
  try {
    await navigator.clipboard.writeText(jsonText.value);
    copied.value = true;
    setTimeout(() => (copied.value = false), 1500);
  } catch {
    const ta = document.createElement('textarea');
    ta.value = jsonText.value;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand('copy');
    document.body.removeChild(ta);
    copied.value = true;
    setTimeout(() => (copied.value = false), 1500);
  }
}

/** --- Minimal formatting helpers --- */
function colorForPrimitive(v) {
  const t = typeof v;
  if (t === 'string') return '#C17D00';     // amber-ish for strings
  if (t === 'number') return '#2A7ACC';     // blue for numbers
  if (t === 'boolean') return '#B73999';    // magenta for booleans
  return '#6A6F7A';                         // gray for null
}
function typeLabel(v) {
  if (v === null) return 'null';
  if (isArray(v)) return `Array(${v.length})`;
  if (isObject(v)) return 'Object';
  return typeof v;
}

/** --- Local component: JsonNode --- */
const JsonNode = defineComponent({
  name: 'JsonNode',
  props: {
    node: { type: null, required: true },
    indent: { type: Number, default: 2 },
    sortKeys: { type: Boolean, default: false },
    collapsible: { type: Boolean, default: true },
    quoteKeys: { type: Boolean, default: false },
    level: { type: Number, default: 0 },
    /** internal: force re-render when version changes */
    version: { type: Number, default: 0 }
  },
  setup(p) {
    return () => {
      // reference version so Vue tracks it (forces re-render on toggle)
      void p.version;

      const n = p.node;

      // Primitive
      if (typeof n !== 'object' || n === null) {
        const color = colorForPrimitive(n);
        return h('span', { class: 'json-primitive', style: { color } }, String(n));
      }

      // Array
      if (Array.isArray(n)) {
        const collapsed = isCollapsed(n);
        const header = h('span', { class: 'json-bracket' }, '[');
        const footer = h('span', { class: 'json-bracket' }, ']');

        const toggleBtn = p.collapsible
          ? h(
              'button',
              { class: 'toggle-btn', onClick: () => toggle(n) },
              collapsed ? `▶ ${typeLabel(n)}` : `▼ ${typeLabel(n)}`
            )
          : null;

        if (collapsed) {
          return h('div', { class: 'json-node' }, [toggleBtn, ' ', header, '…', footer]);
        }

        const children = n.map((item, idx) =>
          h('div', { class: 'json-line', style: { paddingLeft: `${p.indent}px` } }, [
            h(JsonNode, {
              node: item,
              indent: p.indent,
              sortKeys: p.sortKeys,
              collapsible: p.collapsible,
              quoteKeys: p.quoteKeys,
              level: p.level + 1,
              version: p.version
            }),
            idx < n.length - 1 ? h('span', { class: 'json-comma' }, ',') : null
          ])
        );

        return h('div', { class: 'json-node' }, [toggleBtn, ' ', header, ...children, footer]);
      }

      // Object
      const collapsed = isCollapsed(n);
      const keys = getKeys(n, p.sortKeys);
      const header = h('span', { class: 'json-brace' }, '{');
      const footer = h('span', { class: 'json-brace' }, '}');

      const toggleBtn = p.collapsible
        ? h(
            'button',
            { class: 'toggle-btn', onClick: () => toggle(n) },
            collapsed ? `▶ ${typeLabel(n)}` : `▼ ${typeLabel(n)}`
          )
        : null;

      if (collapsed) {
        return h('div', { class: 'json-node' }, [toggleBtn, ' ', header, '…', footer]);
      }

      const lines = keys.map((k, idx) => {
        const keyText = p.quoteKeys ? `"${k}"` : k;
        return h('div', { class: 'json-line', style: { paddingLeft: `${p.indent}px` } }, [
          h('span', { class: 'json-key' }, keyText),
          h('span', { class: 'json-colon' }, ': '),
          h(JsonNode, {
            node: n[k],
            indent: p.indent,
            sortKeys: p.sortKeys,
            collapsible: p.collapsible,
            quoteKeys: p.quoteKeys,
            level: p.level + 1,
            version: p.version
          }),
          idx < keys.length - 1 ? h('span', { class: 'json-comma' }, ',') : null
        ]);
      });

      return h('div', { class: 'json-node' }, [toggleBtn, ' ', header, ...lines, footer]);
    };
  }
});
</script>

<template>
  <div class="json-viewer">
    <div class="toolbar" v-if="showCopy">
      <button class="copy-btn" @click="copyJson">
        {{ copied ? 'Copied!' : 'Copy JSON' }}
      </button>
    </div>

    <div class="json-root">
      <!-- version is passed to force child updates when collapse toggles -->
      <JsonNode
        :node="value"
        :indent="indent"
        :sort-keys="sortKeys"
        :collapsible="collapsible"
        :quote-keys="quoteKeys"
        :level="0"
        :version="version"
      />
    </div>
  </div>
</template>

<style scoped>
.json-viewer {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #1f2937; /* gray-800 */
}

.toolbar {
  margin-bottom: 8px;
}

.copy-btn {
  border: 1px solid #cbd5e1; /* gray-300 */
  background: #f8fafc; /* gray-50 */
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
}
.copy-btn:hover {
  background: #f1f5f9; /* gray-100 */
}

.json-root {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
  background: #ffffff;
  overflow-x: auto;
}

.json-node {
  display: block;
}

.json-line {
  white-space: pre-wrap;
}

.toggle-btn {
  font-size: 12px;
  border: none;
  background: transparent;
  color: #374151; /* gray-700 */
  cursor: pointer;
  padding: 0 4px 0 0;
}

.json-brace, .json-bracket {
  color: #111827; /* gray-900 */
  font-weight: 600;
}

.json-key {
  color: #0F766E; /* teal-700 */
}
.json-colon, .json-comma {
  color: #6B7280; /* gray-500 */
}

.json-primitive {
  /* color applied inline via style for type colors */
}
</style>
