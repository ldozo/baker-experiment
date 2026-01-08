
<script setup>
import JsonNode from './JsonNode.vue';

const props = defineProps({
  value: { type: [Object, Array], required: true },
  collapsed: { type: Boolean, default: false },
  collapseDepth: { type: Number, default: 1 },
  /** Limit height from outside if needed */
  maxHeight: { type: String, default: 'max-h-96' } // e.g., 'max-h-64' | 'max-h-80' | 'max-h-96'
});
</script>

<template>
  <div
    :class="[
      // Container: full width, but never push page horizontally
      'w-full max-w-full rounded-lg border border-slate-200 bg-white shadow-sm p-4 font-mono text-sm',
      // Vertical control: internal scroll; horizontal hidden
      maxHeight,
      'overflow-y-auto overflow-x-hidden'
    ]"
  >
    <JsonNode
      :data="value"
      :keyName="null"
      :level="0"
      :collapsed="collapsed"
      :collapseDepth="collapseDepth"
    />
  </div>
</template>
