<template>
  <main
    class="grid min-h-screen place-items-center bg-zinc-900 px-6 py-24 sm:py-32 lg:px-8"
  >
    <div class="flex flex-col items-center">
      <img :src="`/assets/banner.png`" class="h-12 w-auto" />
      <p class="mt-6 max-w-sm text-base text-center leading-7 text-zinc-400">
        Join Minecraft, and use the command <br />
        <span class="text-cyan-200">/authorize &lt;code&gt;</span> to link your
        account.
      </p>
      <h1
        v-if="code"
        class="mt-4 text-3xl font-bold tracking-tight text-cyan-200 space-x-4 sm:text-5xl font-mono"
      >
        <span v-for="letter in code?.split('')">{{ letter }}</span>
      </h1>
      <svg
        v-else
        aria-hidden="true"
        class="mt-4 size-12 text-transparent animate-spin fill-cyan-200"
        viewBox="0 0 100 101"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
          fill="currentColor"
        />
        <path
          d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
          fill="currentFill"
        />
      </svg>

      <div class="mt-5 w-72">
        <div class="relative">
          <div class="absolute inset-0 flex items-center" aria-hidden="true">
            <div class="w-full border-t border-zinc-700" />
          </div>
          <div class="relative flex justify-center text-sm font-bold leading-6">
            <span class="bg-zinc-900 px-6 text-zinc-100">Or continue with</span>
          </div>
        </div>

        <div class="mt-3 px-2">
          <NuxtLink
            :href="{ path: '/signin', query: route.query }"
            class="rounded-md inline-flex items-center justify-center ring-1 ring-zinc-800 px-3.5 py-2.5 w-full h-8 text-sm font-semibold text-white shadow-sm bg-zinc-800 hover:bg-zinc-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-zinc-600"
            >Login to your account</NuxtLink
          >
        </div>
      </div>

      <div v-if="error" class="mt-6 rounded-md bg-red-600/5 p-4">
        <div class="flex">
          <div class="flex-shrink-0">
            <XCircleIcon class="h-5 w-5 text-red-400" aria-hidden="true" />
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-bold text-red-400">
              {{ error }}
            </h3>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { XCircleIcon } from "@heroicons/vue/20/solid";
import linkWebsocket from "~/utils/link-ws";
definePageMeta({
  layout: false,
});

const code = ref<string | undefined>();
const error = ref<string | undefined>();
const route = useRoute();
const router = useRouter();

linkWebsocket.listen((message) => {
  console.log(message);
  const [control, ...content] = message.split(":");
  switch (control) {
    case "code":
      code.value = content[0];
      break;
    case "connect":
      code.value = undefined;

      const token = useSessionToken();
      token.value = content[0];
      updateUser()
        .catch((e) => {
          error.value = e.statusMessage;
        })
        .then(() => {
          const redirect = route.query.redirect?.toString();
          if (redirect) {
            router.push(redirect);
          } else {
            router.push("/");
          }
        });

      break;
  }
});
linkWebsocket.error((e) => {
  error.value = e.statusMessage;
});

useHead({
  title: "Link your account",
});
</script>
