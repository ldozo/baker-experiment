<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-emerald-100">
    <div class="bg-white p-6 rounded-lg shadow-lg text-emerald-700 font-bold">
      <RouterView />
    </div>
  </div>
</template>

<style scoped></style>
