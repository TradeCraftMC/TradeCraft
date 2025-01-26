<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
  <ModalStack />
</template>

<script setup lang="ts">
import type { Currency } from "./types/currency";

// Pull configuration
const config = useAPIConfig();
config.value = await $fetch("/assets/config.json");

// Load materials
loadMaterials();

// Load currencies
const currencies = useCurrencies();
const currentCurrency = useCurrentCurrency();
currencies.value = await $fetch<Array<Currency>>("/api/v1/currency");
currentCurrency.value = currencies.value[0]?.id;

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
