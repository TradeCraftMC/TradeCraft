<template>
  <div class="bg-zinc-900 min-h-screen flex flex-col">
    <!-- Mobile menu -->
    <TransitionRoot as="template" :show="open">
      <Dialog class="relative z-40 lg:hidden" @close="open = false">
        <TransitionChild
          as="template"
          enter="transition-opacity ease-linear duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="transition-opacity ease-linear duration-300"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-zinc-900/25" />
        </TransitionChild>

        <div class="fixed inset-0 z-40 flex">
          <TransitionChild
            as="template"
            enter="transition ease-in-out duration-300 transform"
            enter-from="-translate-x-full"
            enter-to="translate-x-0"
            leave="transition ease-in-out duration-300 transform"
            leave-from="translate-x-0"
            leave-to="-translate-x-full"
          >
            <DialogPanel
              class="relative flex w-full max-w-xs flex-col overflow-y-auto bg-zinc-900 pb-12 shadow-xl"
            >
              <div class="flex px-4 pb-2 pt-5">
                <button
                  type="button"
                  class="-m-2 inline-flex items-center justify-center rounded-md p-2 text-zinc-400"
                  @click="open = false"
                >
                  <span class="sr-only">Close menu</span>
                  <XMarkIcon class="size-6" aria-hidden="true" />
                </button>
              </div>

              <!-- Links -->
              <TabGroup as="div" class="mt-2">
                <div class="border-b border-zinc-600">
                  <TabList class="-mb-px flex space-x-8 px-4">
                    <Tab
                      as="template"
                      v-for="category in navigation.categories"
                      :key="category.name"
                      v-slot="{ selected }"
                    >
                      <button
                        :class="[
                          selected
                            ? 'border-cyan-200 text-cyan-200'
                            : 'border-transparent text-zinc-600',
                          'flex-1 whitespace-nowrap border-b-2 px-1 py-4 text-base font-medium',
                        ]"
                      >
                        {{ category.name }}
                      </button>
                    </Tab>
                  </TabList>
                </div>
                <TabPanels as="template">
                  <TabPanel
                    v-for="category in navigation.categories"
                    :key="category.name"
                    class="space-y-12 px-4 py-6"
                  >
                    <div class="grid grid-cols-2 gap-x-4 gap-y-10">
                      <div
                        v-for="item in category.featured"
                        :key="item.name"
                        class="group relative"
                      >
                        <img
                          :src="item.imageSrc"
                          :alt="item.imageAlt"
                          class="transition aspect-square w-full rounded-md bg-zinc-800 object-cover group-hover:opacity-75"
                        />
                        <a
                          :href="item.href"
                          class="mt-6 block text-sm font-medium text-zinc-100"
                        >
                          <span
                            class="absolute inset-0 z-10"
                            aria-hidden="true"
                          />
                          {{ item.name }}
                        </a>
                        <p
                          aria-hidden="true"
                          class="mt-1 text-sm text-zinc-400"
                        >
                          Shop now
                        </p>
                      </div>
                    </div>
                  </TabPanel>
                </TabPanels>
              </TabGroup>

              <div class="space-y-6 border-t border-zinc-700 px-4 py-6">
                <div
                  v-for="page in navigation.pages"
                  :key="page.name"
                  class="flow-root"
                >
                  <a
                    :href="page.href"
                    class="-m-2 block p-2 font-medium text-zinc-100"
                    >{{ page.name }}</a
                  >
                </div>
              </div>

              <div
                v-if="!user"
                class="space-y-6 border-t border-zinc-700 px-4 py-6"
              >
                <div class="flow-root">
                  <NuxtLink
                    href="/link"
                    class="-m-2 block p-2 font-medium text-zinc-100"
                    >Link account</NuxtLink
                  >
                </div>
                <div class="flow-root">
                  <NuxtLink
                    href="/signin"
                    class="-m-2 block p-2 font-medium text-zinc-100"
                    >Sign in</NuxtLink
                  >
                </div>
              </div>
              <div
                v-else-if="user.admin"
                class="border-t border-zinc-700 px-4 py-6"
              >
                <div v-if="user.admin" class="flow-root">
                  <NuxtLink
                    href="/admin"
                    class="-m-2 block p-2 font-medium text-zinc-100"
                    >Admin Dashboard</NuxtLink
                  >
                </div>
              </div>

              <div class="space-y-6 border-t border-zinc-700 px-4 py-6">
                <!-- Currency selector -->
                <form>
                  <div class="-ml-2 inline-grid grid-cols-1">
                    <select
                      id="mobile-currency"
                      name="currency"
                      aria-label="Currency"
                      class="col-start-1 row-start-1 w-full appearance-none rounded-md bg-zinc-900 py-0.5 pl-2 pr-7 text-base font-medium text-zinc-100 focus:outline focus:outline-2 group-hover:text-zinc-400 sm:text-sm/6"
                      v-model="currentCurrency"
                    >
                      <option
                        v-for="currency in currencies"
                        :key="currency.id"
                        :value="currency.id"
                      >
                        {{ currency.identifier }} ({{ currency.sign }})
                      </option>
                    </select>
                    <ChevronDownIcon
                      class="pointer-events-none col-start-1 row-start-1 mr-1 size-5 self-center justify-self-end fill-zinc-400"
                      aria-hidden="true"
                    />
                  </div>
                </form>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </Dialog>
    </TransitionRoot>

    <header class="relative">
      <nav aria-label="Top">
        <!-- Top navigation -->
        <div class="bg-zinc-950">
          <div
            class="mx-auto flex h-10 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8"
          >
            <!-- Currency selector -->
            <form>
              <div class="-ml-2 inline-grid grid-cols-1">
                <select
                  id="desktop-currency"
                  name="currency"
                  aria-label="Currency"
                  class="col-start-1 row-start-1 w-full appearance-none rounded-md bg-zinc-900 py-0.5 pl-2 pr-7 text-left text-base font-medium text-white focus:outline focus:outline-2 focus:-outline-offset-1 focus:outline-white sm:text-sm/6"
                  v-model="currentCurrency"
                >
                  <option v-if="currentCurrency == undefined" disabled value="">
                    No currencies available
                  </option>
                  <option
                    v-for="currency in currencies"
                    :key="currency.id"
                    :value="currency.id"
                  >
                    {{ currency.identifier }} ({{ currency.sign }})
                  </option>
                </select>
                <ChevronDownIcon
                  class="pointer-events-none col-start-1 row-start-1 mr-1 size-5 self-center justify-self-end fill-gray-300"
                  aria-hidden="true"
                />
              </div>
            </form>

            <div v-if="!user" class="flex items-center space-x-6">
              <NuxtLink
                href="/link"
                class="text-sm font-medium text-white hover:text-gray-100"
                >Link account</NuxtLink
              >
              <NuxtLink
                href="/signin"
                class="text-sm font-medium text-white hover:text-gray-100"
                >Sign in</NuxtLink
              >
            </div>
            <div v-else class="flex items-center">
              <p class="text-sm font-medium text-zinc-400">
                Hello, {{ user.name ?? user.playerUUID }}
              </p>
              <NuxtLink
                href="/dashboard"
                class="ml-6 hidden lg:block text-sm font-medium text-white hover:text-gray-100"
                >Dashboard</NuxtLink
              >
              <NuxtLink
                v-if="user.admin"
                href="/admin"
                class="ml-6 text-sm font-medium text-white hover:text-gray-100"
                >Admin</NuxtLink
              >
            </div>
          </div>
        </div>

        <!-- Secondary navigation -->
        <div>
          <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <div class="border-b border-zinc-700">
              <div class="flex h-16 items-center justify-between">
                <!-- Logo (lg+) -->
                <div class="hidden lg:flex lg:flex-1 lg:items-center">
                  <NuxtLink href="/">
                    <span class="sr-only">{{ config.appName }}</span>
                    <img
                      class="h-8 w-auto"
                      :src="`/assets/banner.png`"
                      alt=""
                    />
                  </NuxtLink>
                </div>

                <div class="hidden h-full lg:flex">
                  <!-- Flyout menus -->
                  <PopoverGroup class="inset-x-0 bottom-0 px-4">
                    <div class="flex h-full justify-center space-x-8">
                      <Popover
                        v-for="category in navigation.categories"
                        :key="category.name"
                        class="flex"
                        v-slot="{ open }"
                      >
                        <div class="relative flex">
                          <PopoverButton
                            :class="[
                              open
                                ? 'border-cyan-200 text-cyan-200'
                                : 'border-transparent text-zinc-400 hover:text-zinc-100',
                              'cursor-pointer relative z-10 -mb-px flex items-center border-b-2 pt-px text-sm font-medium transition-colors duration-200 ease-out',
                            ]"
                            >{{ category.name }}</PopoverButton
                          >
                        </div>

                        <transition
                          enter-active-class="transition ease-out duration-200"
                          enter-from-class="opacity-0"
                          enter-to-class="opacity-100"
                          leave-active-class="transition ease-in duration-150"
                          leave-from-class="opacity-100"
                          leave-to-class="opacity-0"
                        >
                          <PopoverPanel
                            class="absolute inset-x-0 top-full text-sm text-zinc-400"
                          >
                            <!-- Presentational element used to render the bottom shadow, if we put the shadow on the actual panel it pokes out the top, so we use this shorter element to hide the top of the shadow -->
                            <div
                              class="absolute inset-0 top-1/2 bg-zinc-900 shadow shadow-zinc-800"
                              aria-hidden="true"
                            />

                            <div class="relative bg-zinc-900">
                              <div class="mx-auto max-w-7xl px-8">
                                <div
                                  class="grid grid-cols-4 gap-x-8 gap-y-10 py-16"
                                >
                                  <div
                                    v-for="item in category.featured"
                                    :key="item.name"
                                    class="group relative"
                                  >
                                    <img
                                      :src="item.imageSrc"
                                      :alt="item.imageAlt"
                                      class="transition aspect-square w-full rounded-md bg-gray-100 object-cover group-hover:opacity-75"
                                    />
                                    <a
                                      :href="item.href"
                                      class="mt-4 block font-medium text-zinc-100"
                                    >
                                      <span
                                        class="absolute inset-0 z-10"
                                        aria-hidden="true"
                                      />
                                      {{ item.name }}
                                    </a>
                                    <p aria-hidden="true" class="mt-1">
                                      Shop now
                                    </p>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </PopoverPanel>
                        </transition>
                      </Popover>

                      <a
                        v-for="page in navigation.pages"
                        :key="page.name"
                        :href="page.href"
                        class="flex items-center text-sm font-medium text-zinc-400 hover:text-zinc-100"
                        >{{ page.name }}</a
                      >
                    </div>
                  </PopoverGroup>
                </div>

                <!-- Mobile menu and search (lg-) -->
                <div class="flex flex-1 items-center lg:hidden">
                  <button
                    type="button"
                    class="-ml-2 rounded-md p-2 text-zinc-400"
                    @click="open = true"
                  >
                    <span class="sr-only">Open menu</span>
                    <Bars3Icon class="size-6" aria-hidden="true" />
                  </button>

                  <!-- Search -->
                  <a
                    href="#"
                    class="ml-2 p-2 text-gray-400 hover:text-gray-500"
                  >
                    <span class="sr-only">Search</span>
                    <MagnifyingGlassIcon class="size-6" aria-hidden="true" />
                  </a>
                </div>

                <!-- Logo (lg-) -->
                <NuxtLink href="/" class="lg:hidden">
                  <span class="sr-only">{{ config.appName }}</span>
                  <img :src="`/assets/banner.png`" class="h-8 w-auto" />
                </NuxtLink>

                <div class="flex flex-1 items-center justify-end">
                  <a
                    href="#"
                    class="hidden text-sm font-medium text-zinc-400 hover:text-zinc-100 lg:block"
                    >Search</a
                  >

                  <div class="flex items-center lg:ml-8">
                    <!-- Help -->
                    <a
                      href="#"
                      class="p-2 text-gray-400 hover:text-gray-500 lg:hidden"
                    >
                      <span class="sr-only">Help</span>
                      <QuestionMarkCircleIcon
                        class="size-6"
                        aria-hidden="true"
                      />
                    </a>
                    <a
                      href="#"
                      class="hidden text-sm font-medium text-zinc-400 hover:text-zinc-100 lg:block"
                      >Help</a
                    >

                    <!-- Cart -->
                    <div class="ml-4 flow-root lg:ml-8">
                      <a href="#" class="group -m-2 flex items-center p-2">
                        <ShoppingBagIcon
                          class="size-6 shrink-0 text-zinc-400 group-hover:text-zinc-100"
                          aria-hidden="true"
                        />
                        <span
                          class="ml-2 text-sm font-medium text-zinc-400 group-hover:text-zinc-100"
                          >0</span
                        >
                        <span class="sr-only">items in cart, view bag</span>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </nav>
    </header>

    <main class="grow">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  Dialog,
  DialogPanel,
  Popover,
  PopoverButton,
  PopoverGroup,
  PopoverPanel,
  Tab,
  TabGroup,
  TabList,
  TabPanel,
  TabPanels,
  TransitionChild,
  TransitionRoot,
} from "@headlessui/vue";
import {
  Bars3Icon,
  MagnifyingGlassIcon,
  QuestionMarkCircleIcon,
  ShoppingBagIcon,
  XMarkIcon,
} from "@heroicons/vue/24/outline";
import { ChevronDownIcon } from "@heroicons/vue/20/solid";
import type { Currency } from "~/types/currency";

const navigation = {
  categories: [
    {
      name: "Women",
      featured: [
        {
          name: "New Arrivals",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-category-01.jpg",
          imageAlt:
            "Models sitting back to back, wearing Basic Tee in black and bone.",
        },
        {
          name: "Basic Tees",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-category-02.jpg",
          imageAlt:
            "Close up of Basic Tee fall bundle with off-white, ochre, olive, and black tees.",
        },
        {
          name: "Accessories",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-category-03.jpg",
          imageAlt:
            "Model wearing minimalist watch with black wristband and white watch face.",
        },
        {
          name: "Carry",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-category-04.jpg",
          imageAlt:
            "Model opening tan leather long wallet with credit card pockets and cash pouch.",
        },
      ],
    },
    {
      name: "Men",
      featured: [
        {
          name: "New Arrivals",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-01-men-category-01.jpg",
          imageAlt:
            "Hats and sweaters on wood shelves next to various colors of t-shirts on hangers.",
        },
        {
          name: "Basic Tees",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-01-men-category-02.jpg",
          imageAlt: "Model wearing light heather gray t-shirt.",
        },
        {
          name: "Accessories",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-01-men-category-03.jpg",
          imageAlt:
            "Grey 6-panel baseball hat with black brim, black mountain graphic on front, and light heather gray body.",
        },
        {
          name: "Carry",
          href: "#",
          imageSrc:
            "https://tailwindui.com/plus/img/ecommerce-images/mega-menu-01-men-category-04.jpg",
          imageAlt:
            "Model putting folded cash into slim card holder olive leather wallet with hand stitching.",
        },
      ],
    },
  ],
  pages: [
    { name: "Company", href: "#" },
    { name: "Stores", href: "#" },
  ],
};

const open = ref(false);

const user = useUser();
const config = useAPIConfig();

const currencies = useCurrencies();
const currentCurrency = useCurrentCurrency();
</script>
