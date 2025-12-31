<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-emerald-100">
    <div class="text-emerald-700 font-bold">
      <RouterView />
    </div>
  </div>
</template>

<style scoped></style>
