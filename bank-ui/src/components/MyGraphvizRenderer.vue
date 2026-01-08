<script setup>
import { ref, computed, onMounted, watch, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { select, transition } from 'd3'
import { graphviz } from 'd3-graphviz'

const props = defineProps({
  /** Provide DOT directly */
  value: { type: String, default: '' },
  /** Or fetch DOT from endpoint (text/plain or JSON with { dot }) */
  src: { type: String, default: '' },
  jsonDotPath: { type: String, default: 'dot' },
  engine: { type: String, default: 'dot' },              // 'dot' | 'neato' | 'fdp' | 'sfdp' | 'twopi' | 'circo'
  transitionDuration: { type: Number, default: 500 },
  zoom: { type: Boolean, default: true },
  fit: { type: Boolean, default: true },
  refreshMs: { type: Number, default: 0 },
  method: { type: String, default: 'GET' },
  payload: { type: Object, default: () => ({}) },
  headers: { type: Object, default: () => ({}) }
})

const containerRef = ref(null)
const loading = ref(false)
const error = ref('')
const dot = ref('')
let gv = null
let mounted = false
let timer = null

const isClient = computed(() => typeof window !== 'undefined')

function getValueByPath(obj, path) {
  if (!path) return obj
  return path.split('.').reduce((acc, k) => (acc ? acc[k] : undefined), obj)
}

async function fetchDotIfNeeded() {
  if (props.value?.trim()) { dot.value = props.value; return }
  if (!props.src) { dot.value = ''; return }

  loading.value = true; error.value = ''
  try {
    const cfg = { method: props.method, url: props.src, headers: props.headers }
    if (props.method.toUpperCase() === 'GET') cfg.params = props.payload
    else cfg.data = props.payload

    const res = await axios(cfg)
    const ct = res.headers?.['content-type'] ?? ''
    dot.value = ct.includes('application/json')
      ? getValueByPath(res.data, props.jsonDotPath || 'dot')
      : (typeof res.data === 'string' ? res.data : String(res.data))

    if (!dot.value || typeof dot.value !== 'string') throw new Error('DOT string missing in response')
  } catch (e) {
    error.value = e?.message || 'Failed to fetch graph'
    dot.value = ''
  } finally {
    loading.value = false
  }
}

function initRenderer() {
  if (!isClient.value || !containerRef.value) return
  gv = graphviz(containerRef.value, {
    useWorker: false,           // avoids worker path issues in Vite
    wasmFolder: '/hpcc-wasm'    // where graphvizlib.wasm is served
  })
    .engine(props.engine)
    .zoom(props.zoom)
    .transition(() => transition().duration(props.transitionDuration))

  mounted = true
}

async function renderDot() {
  if (!gv || !mounted || !dot.value) {
    if (containerRef.value) containerRef.value.innerHTML = ''
    return
  }
  await gv.renderDot(dot.value)
  if (props.fit) {
    select(containerRef.value).select('svg').attr('width', '100%').attr('height', null)
  }
}

onMounted(async () => {
  if (!isClient.value) return
  initRenderer()
  await fetchDotIfNeeded()
  await renderDot()
  if (props.refreshMs > 0 && props.src) {
    timer = setInterval(async () => { await fetchDotIfNeeded(); await renderDot() }, props.refreshMs)
  }
})

watch(() => [props.value, props.src, props.jsonDotPath, props.method, props.payload, props.headers],
  async () => { await fetchDotIfNeeded(); await renderDot() }, { deep: true })

watch(() => [props.engine, props.zoom, props.transitionDuration],
  async ([engine, zoom, dur]) => {
    if (!gv || !mounted) return
    gv.engine(engine).zoom(zoom).transition(() => transition().duration(dur))
    await renderDot()
  })

onBeforeUnmount(() => { mounted = false; gv = null; if (timer) clearInterval(timer) })
</script>

<template>
  <div class="max-w-80">
    <div v-if="loading" class="status">Loading graph…</div>
    <div v-else-if="error" class="error">Error: {{ error }}</div>
    <div v-else ref="containerRef" class="canvas"></div>
  </div>
</template>
