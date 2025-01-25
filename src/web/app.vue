<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
import type { RouteLocationNormalizedGeneric } from 'vue-router';

const config = useAPIConfig();
config.value = await $fetch("/assets/config.json");

try {
  await updateUser();
} catch {}

useHead({
  titleTemplate(title) {
    if (title) {
      return `${title} | ${config.value.appName}`;
    }
    return `${config.value.appName}`;
  },
});
</script>
