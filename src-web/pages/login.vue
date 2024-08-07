<template>
  <div class="flex min-h-screen flex-1 bg-zinc-900">
    <div
      class="flex flex-1 flex-col justify-center px-4 py-12 sm:px-6 lg:flex-none lg:px-20 xl:px-24"
    >
      <div class="mx-auto w-full max-w-sm lg:w-96">
        <div>
          <img
            class="h-10 w-auto"
            :src="'/api/v1/branding/banner'"
            :alt="appConfig.app_name"
          />
          <h2
            class="mt-8 text-2xl font-bold leading-9 tracking-tight text-zinc-100"
          >
            Sign in to your account
          </h2>
          <p class="mt-2 text-sm leading-6 text-zinc-400">
            Don't have an account?
            {{ " " }}
            <NuxtLink
              href="/link"
              class="font-semibold text-cyan-200 hover:text-cyan-100"
              >Link instead.</NuxtLink
            >
          </p>
        </div>

        <div class="mt-10">
          <div>
            <form @submit.prevent="login" class="space-y-6">
              <div>
                <label
                  for="username"
                  class="block text-sm font-bold leading-6 text-zinc-300"
                  >Username</label
                >
                <div class="mt-2">
                  <input
                    id="username"
                    name="username"
                    type="text"
                    autocomplete="username"
                    required
                    placeholder="myUsername"
                    v-model="username"
                    class="block w-full bg-zinc-800 text-zinc-100 pl-3 rounded-md border-0 py-1.5 shadow-sm ring-1 ring-inset ring-zinc-700 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-cyan-200 sm:text-sm sm:leading-6"
                  />
                </div>
              </div>

              <div>
                <label
                  for="password"
                  class="block text-sm font-bold leading-6 text-zinc-300"
                  >Password</label
                >
                <div class="mt-2">
                  <input
                    id="password"
                    name="password"
                    type="password"
                    autocomplete="current-password"
                    required
                    placeholder="●●●●●●●●●●●●"
                    v-model="password"
                    class="block w-full bg-zinc-800 text-zinc-100 rounded-md border-0 py-1.5 shadow-sm ring-1 ring-inset ring-gray-700 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-cyan-200 sm:text-sm sm:leading-6"
                  />
                </div>
              </div>

              <div>
                <SpinnerButton
                  type="submit"
                  :loading="loading"
                  class="flex w-full h-9 justify-center rounded-md bg-cyan-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-cyan-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-600"
                >
                  Sign in
                </SpinnerButton>
              </div>

              <div
                v-if="error != null"
                class="mt-6 rounded-md bg-red-600/5 p-4"
              >
                <div class="flex">
                  <div class="flex-shrink-0">
                    <XCircleIcon
                      class="h-5 w-5 text-red-400"
                      aria-hidden="true"
                    />
                  </div>
                  <div class="ml-3">
                    <h3 class="text-sm font-bold text-red-400">
                      {{ error }}
                    </h3>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="relative hidden w-0 flex-1 lg:block">
      <img
        class="absolute inset-0 h-full w-full object-cover"
        :src="'/api/v1/branding/login'"
        alt=""
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { XCircleIcon } from "@heroicons/vue/20/solid";
const username = ref("");
const password = ref("");
const loading = ref(false);

const error = ref(null);

const user = useUser();
const router = useRouter();

const appConfig = useState<{ app_name: string }>("appConfig");

function login() {
  loading.value = true;
  $fetch("/api/v1/auth/login", {
    method: "POST",
    body: { username: username.value, password: password.value },
  })
    .then(async () => {
      user.value = await $fetch("/api/v1/auth/fetch");
      router.push("/");
    })
    .catch((err) => {
      error.value =
        err.data ||
        "There was a problem signing you in. Check your username and password and try again.";
      loading.value = false;
    });
}

definePageMeta({
  layout: false,
});

useHead({
  title: "Sign in"
})
</script>
