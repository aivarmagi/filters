<script setup lang="ts">
defineProps<{
  loading: boolean,
  message?: string,
}>()
</script>

<template>
  <div v-if="loading">
    <BSpinner class="me-2" small /> {{ message }}
  </div>
</template>

<style scoped>

</style>