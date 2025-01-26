import type { RouteLocationNormalized } from "vue-router";

export const useCurrentNavigationIndex = (
  navigation: Ref<Array<{ href: string, prefix?: string }>>
) => {
  const router = useRouter();
  const route = useRoute();
  const currentNavigation = ref(-1);

  function calculateCurrentNavIndex(
    to: RouteLocationNormalized,
    nav: typeof navigation.value
  ) {
    const validOptions = nav
      .map((e, i) => ({ ...e, index: i }))
      .filter((e) => to.fullPath.startsWith(e.prefix ?? e.href));
    const bestOption = validOptions
      .sort((a, b) => b.href.length - a.href.length)
      .at(0);

    return bestOption?.index ?? -1;
  }

  currentNavigation.value = calculateCurrentNavIndex(route, navigation.value);

  router.afterEach((to) => {
    currentNavigation.value = calculateCurrentNavIndex(to, navigation.value);
  });

  watch(navigation, (newValue) => {
    currentNavigation.value = calculateCurrentNavIndex(route, newValue);
  });

  return currentNavigation;
};
