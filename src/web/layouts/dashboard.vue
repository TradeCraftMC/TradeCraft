<template>
  <div class="min-h-screen w-screen bg-zinc-950 flex flex-col">
    <!-- Static sidebar for desktop -->
    <div
      class="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-72 lg:flex-col"
    >
      <!-- Sidebar component, swap this element with another sidebar if you like -->
      <div
        class="flex grow flex-col gap-y-5 overflow-y-auto border-r border-zinc-700 bg-zinc-900 px-6"
      >
        <div class="flex h-16 shrink-0 items-center">
          <img
            class="h-8 w-auto"
            :src="`/assets/banner.png`"
            :alt="config.appName"
          />
        </div>
        <nav class="flex flex-1 flex-col">
          <ul role="list" class="flex flex-1 flex-col gap-y-7">
            <li>
              <ul role="list" class="-mx-2 space-y-1">
                <li v-for="(item, itemIdx) in navigation" :key="item.name">
                  <NuxtLink
                    :href="item.href"
                    :class="[
                      itemIdx == currentNavigationIndex
                        ? 'bg-zinc-800 text-cyan-200'
                        : 'text-zinc-400 hover:bg-zinc-800/40 hover:text-cyan-100',
                      'transition group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold',
                    ]"
                  >
                    <div class="relative">
                      <component
                        :is="item.icon"
                        :class="[
                          itemIdx == currentNavigationIndex ? '' : '',
                          'size-6 shrink-0',
                        ]"
                        aria-hidden="true"
                      />
                      <span
                        v-if="item.amount"
                        class="inline-flex items-center justify-center absolute -top-[30%] -right-[30%] bg-cyan-200 rounded-full size-4 text-xs text-zinc-900 pt-[1px]"
                        >{{ item.amount }}</span
                      >
                    </div>
                    {{ item.name }}
                  </NuxtLink>
                </li>
              </ul>
            </li>
            <li class="mt-auto -mx-4 mb-2">
              <Listbox as="div" v-model="currentVendor">
                <div class="relative mt-2">
                  <ListboxButton
                    class="grid w-full cursor-default grid-cols-1 rounded-md bg-zinc-800 py-1.5 pr-2 pl-3 text-left text-zinc-100 outline-1 -outline-offset-1 outline-zinc-700 focus:outline-2 focus:-outline-offset-2 focus:outline-cyan-200 sm:text-sm/6"
                  >
                    <span
                      class="col-start-1 row-start-1 flex items-center gap-3 pr-6"
                    >
                      <img
                        :src="currentVendor.icon"
                        alt=""
                        class="size-5 shrink-0 rounded-full"
                      />
                      <span class="block truncate">{{
                        currentVendor.name
                      }}</span>
                    </span>
                    <ChevronUpDownIcon
                      class="col-start-1 row-start-1 size-5 self-center justify-self-end text-gray-500 sm:size-4"
                      aria-hidden="true"
                    />
                  </ListboxButton>

                  <transition
                    leave-active-class="transition ease-in duration-100"
                    leave-from-class="opacity-100"
                    leave-to-class="opacity-0"
                  >
                    <ListboxOptions
                      class="absolute bottom-0 z-10 mt-1 max-h-56 w-full overflow-auto rounded-md bg-zinc-900 py-1 text-base ring-1 shadow-lg ring-white/5 focus:outline-hidden sm:text-sm"
                    >
                      <ListboxOption
                        as="template"
                        v-for="vendor in vendors"
                        :key="vendor.id"
                        :value="vendor"
                        v-slot="{ active, selected }"
                      >
                        <li
                          :class="[
                            active
                              ? 'bg-cyan-200 text-zinc-900 outline-hidden'
                              : 'text-zinc-400',
                            'relative cursor-default py-2 pr-9 pl-3 select-none',
                          ]"
                        >
                          <div class="flex items-center">
                            <img
                              :src="vendor.icon"
                              alt=""
                              class="size-5 shrink-0 rounded-full"
                            />
                            <span
                              :class="[
                                selected ? 'font-semibold' : 'font-normal',
                                'ml-3 block truncate',
                              ]"
                              >{{ vendor.name }}</span
                            >
                          </div>

                          <span
                            v-if="selected"
                            :class="[
                              active ? 'text-zinc-900' : 'text-cyan-200',
                              'absolute inset-y-0 right-0 flex items-center pr-4',
                            ]"
                          >
                            <CheckIcon class="size-5" aria-hidden="true" />
                          </span>
                        </li>
                      </ListboxOption>
                    </ListboxOptions>
                  </transition>
                </div>
              </Listbox>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <main class="hidden lg:block py-10 lg:pl-72">
      <div class="px-4 sm:px-6 lg:px-8">
        <slot />
      </div>
    </main>
    <main class="flex grow lg:hidden items-center justify-center">
      <div class="text-center">
        <h1
          class="mt-4 text-3xl font-semibold tracking-tight text-balance text-zinc-100"
        >
          Mobile devices unsupported
        </h1>
        <p class="mt-6 font-medium text-pretty text-zinc-400 max-w-sm">
          Unfortunately, the dashboard is currently unsupported on mobile
          devices.
        </p>
        <div class="mt-10 flex items-center justify-center gap-x-6">
          <NuxtLink
            href="/"
            class="rounded-md bg-cyan-200 px-3.5 py-2.5 text-sm font-semibold text-zinc-900 shadow-xs focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
            >Go back home</NuxtLink
          >
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  Listbox,
  ListboxButton,
  ListboxOption,
  ListboxOptions,
} from "@headlessui/vue";
import {
  ChartPieIcon,
  Cog6ToothIcon,
  HomeIcon,
  UsersIcon,
  ChevronUpDownIcon,
  CheckIcon,
  TagIcon,
  InboxIcon,
  CurrencyDollarIcon,
  AtSymbolIcon,
} from "@heroicons/vue/24/outline";

enum DashboardMode {
  UserVendor,
  Company,
}

const router = useRouter();

const user = useUser();
const vendors = [
  {
    id: "dkasldkas",
    name: "Personal",
    icon: `https://mc-heads.net/head/${user.value?.playerUUID}`,
    type: DashboardMode.UserVendor,
  },
  {
    id: "DJAKSjdasjkd",
    name: "Globe Foundation",
    icon: "",
    type: DashboardMode.Company,
  },
];
const currentVendor = ref(vendors[0]);
const dashboardMode = computed(() => currentVendor.value.type);

const route = useRoute();

const navigation = computed(() =>
  [
    { name: "Dashboard", href: "/dashboard", icon: HomeIcon },
    dashboardMode.value == DashboardMode.Company && {
      name: "Partners",
      href: "/dashboard/partners",
      icon: UsersIcon,
    },
    {
      name: "Orders",
      href: "/dashboard/orders",
      icon: InboxIcon,
      amount: 3,
    },
    {
      name: "Products",
      href: "/dashboard/products",
      icon: TagIcon,
    },
    {
      name: "Marketing",
      href: "/dashboard/marketing",
      icon: AtSymbolIcon,
    },
    {
      name: "Finances",
      href: "/dashboard/finances",
      icon: CurrencyDollarIcon,
    },
    {
      name: "Customisation",
      href: "/dashboard/customisation",
      icon: Cog6ToothIcon,
    },
    { name: "Analytics", href: "/dashboard/analytics", icon: ChartPieIcon },
  ].filter((e) => e !== false)
);
const currentNavigationIndex = useCurrentNavigationIndex(navigation);

function leaveDeadPages() {
  if (navigation.value.findIndex((e) => e.href == route.path) == -1) {
    router.push(navigation.value[0].href);
  }
}

watch(dashboardMode, () => {
  leaveDeadPages();
});

const teams = [
  { id: 1, name: "Heroicons", href: "#", initial: "H", current: false },
  { id: 2, name: "Tailwind Labs", href: "#", initial: "T", current: false },
  { id: 3, name: "Workcation", href: "#", initial: "W", current: false },
];

const sidebarOpen = ref(false);
const config = useAPIConfig();
</script>
