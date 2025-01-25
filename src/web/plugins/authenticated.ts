import type { RouteLocationNormalizedGeneric } from "vue-router";

export default defineNuxtPlugin((nuxtApp) => {
  // Skip plugin when rendering error page
  if (nuxtApp.payload.error) {
    return {};
  }

  const user = useUser();
  const isLoggedIn = computed(() => user != undefined);

  function checkRedirect(
    to: RouteLocationNormalizedGeneric,
    isLoggedIn: boolean
  ) {
    if (to.meta.auth && !isLoggedIn) {
      return navigateTo({ path: "/link", query: { redirect: to.fullPath } });
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
