<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
const config = useAPIConfig();
config.value = await $fetch("/assets/config.json");

useHead({
  titleTemplate(title) {
    if (title) {
      return `${title} | ${config.value.appName}`;
    }
    return `${config.value.appName}`;
  },
});
</script>
