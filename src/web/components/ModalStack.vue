<template>
  <component
    v-for="(modal, modalIdx) in stack"
    :is="modal.component"
    :z-height="(modalIdx + 1) * 50"
    :loading="modal.loading"
    :data="modal.data"
    @event="(event: string) => handleCallback(modalIdx, event)"
  />
</template>

<script setup lang="ts">
const stack = useModalStack();

async function handleCallback(modalIdx: number, event: string) {
  const modal = stack.value[modalIdx];
  const close = () => {
    stack.value.splice(modalIdx, 1);
  };

  // Gets unwrapped when we call from the DOM
  // I kinda hate this but it's how Vue works so....
  (modal.loading as unknown as boolean) = true;
  try {
    await modal.callback(event, close);
  } finally {
    (modal.loading as unknown as boolean) = false;
  }
}
</script>
