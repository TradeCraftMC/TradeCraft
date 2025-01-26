import type { RouteLocationNormalizedGeneric } from "vue-router";

export default defineNuxtPlugin(async (nuxtApp) => {
  // Skip plugin when rendering error page
  if (nuxtApp.payload.error) {
    return;
  }

  const user = useUser();
  if (user.value === undefined) {
    try {
      await updateUser();
    } catch {}
  }
  const isLoggedIn = computed(() => !!user.value);

  function checkRedirect(
    to: RouteLocationNormalizedGeneric,
    loggedIn: boolean
  ) {
    if (to.meta.auth && !loggedIn) {
      return navigateTo({ path: "/link", query: { redirect: to.fullPath } });
    }
    if (loggedIn && to.meta.admin && user.value?.admin == false) {
      return navigateTo("/noauth");
    }
    return undefined;
  }

  addRouteMiddleware(
    "auth",
    (to) => {
      const redirect = checkRedirect(to, isLoggedIn.value);
      if (redirect) return redirect;
    },
    { global: true }
  );

  const currentRoute = useRoute();
  if (import.meta.client) {
    watch(isLoggedIn, (loggedIn) => {
      const redirect = checkRedirect(currentRoute, loggedIn);
      if (redirect) return redirect;
    });
  }
});
