
<script>
export default {
  name: 'JsonNode',
  props: {
    data: { type: [Object, Array, String, Number, Boolean, null], required: true },
    keyName: { type: [String, null], default: null },
    level: { type: Number, required: true },
    collapsed: { type: Boolean, default: false },
    collapseDepth: { type: Number, default: 1 }
  },
  data() {
    return {
      localCollapsed: this.level >= this.collapseDepth ? this.collapsed : false
    };
  },
  computed: {
    nodeType() {
      if (this.data === null) return 'null';
      if (Array.isArray(this.data)) return 'array';
      return typeof this.data;
    },
    entries() {
      if (this.nodeType !== 'object') return [];
      return Object.entries(this.data);
    },
    bracketOpen() {
      return this.nodeType === 'array' ? '[' : '{';
    },
    bracketClose() {
      return this.nodeType === 'array' ? ']' : '}';
    }
  }
};
</script>

<template>
  <div class="w-full max-w-full">
    <!-- Object or Array -->
    <template v-if="nodeType === 'object' || nodeType === 'array'">
      <div class="flex items-center min-w-0">
        <button
          class="inline-flex items-center gap-2 rounded px-1 py-0.5 hover:bg-slate-50"
          @click="localCollapsed = !localCollapsed"
        >
          <span class="text-slate-700">{{ localCollapsed ? '▸' : '▾' }}</span>
          <span class="text-slate-700">
            <!-- Wrap long keys -->
            <span v-if="keyName !== null" class="break-words">
              {{ keyName }}:
            </span>
            <span class="text-slate-900">{{ bracketOpen }}</span>
          </span>
        </button>
      </div>

      <!-- Nested content area -->
      <div
        v-if="!localCollapsed"
        class="pl-6 border-l border-dashed border-slate-200 overflow-x-hidden"
      >
        <template v-if="nodeType === 'array'">
          <div v-for="(item, index) in data" :key="index" class="flex min-w-0">
            <JsonNode
              :data="item"
              :keyName="null"
              :level="level + 1"
              :collapsed="collapsed"
              :collapseDepth="collapseDepth"
            />
          </div>
        </template>
        <template v-else>
          <div v-for="[k, v] in entries" :key="k" class="flex min-w-0">
            <JsonNode
              :data="v"
              :keyName="k"
              :level="level + 1"
              :collapsed="collapsed"
              :collapseDepth="collapseDepth"
            />
          </div>
        </template>

        <div class="text-slate-900">{{ bracketClose }}</div>
      </div>
    </template>

    <!-- Primitive -->
    <template v-else>
      <div class="flex items-start min-w-0">
        <!-- Key -->
        <div v-if="keyName !== null" class="mr-2 text-slate-700 break-words min-w-0">
          <span class="text-teal-700">{{ keyName }}</span>:
        </div>

        <!-- Value -->
        <span
          :class="[
            // Avoid horizontal expansion from long strings/tokens:
            // - break-words wraps at word boundaries
            // - break-all forces wrap even on long continuous tokens
            // - whitespace-pre-wrap preserves newlines while wrapping
            'rounded px-1 whitespace-pre-wrap break-words break-all',
            nodeType === 'string' ? 'text-rose-700' :
            nodeType === 'number' ? 'text-blue-700' :
            nodeType === 'boolean' ? 'text-violet-700' :
            'text-slate-500'
          ]"
        >
          {{ nodeType === 'string' ? '"' + data + '"' : (nodeType === 'null' ? 'null' : data) }}
        </span>
      </div>
    </template>
  </div>
</template>

<style scoped>
/* Indentation guide spacing */
.pl-6 {
  margin-left: 0.25rem;
  padding-left: 1rem;
}
</style>
