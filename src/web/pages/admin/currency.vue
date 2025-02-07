<template>
  <div class="px-4 sm:px-6 lg:px-8">
    <div class="sm:flex sm:items-center">
      <div class="sm:flex-auto">
        <h1 class="text-base font-semibold text-zinc-100">Currencies</h1>
        <p class="mt-2 text-sm text-zinc-400 max-w-md">
          Currencies are interchangable but differently-valued counters of
          wealth. Players can buy and sell anything in any given currency,
          unless the currency isn't "exchangable".
        </p>
      </div>
      <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
        <button
          @click="() => openCreate()"
          type="button"
          class="cursor-pointer block rounded-md bg-cyan-200 px-3 py-2 text-center text-sm font-semibold text-zinc-900 shadow-xs hover:bg-cyan-300 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-200"
        >
          Create
        </button>
      </div>
    </div>
    <div class="mt-8 flow-root">
      <div class="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
        <div class="inline-block min-w-full py-2 align-middle sm:px-6 lg:px-8">
          <table class="min-w-full divide-y divide-zinc-600">
            <thead>
              <tr>
                <th
                  scope="col"
                  class="text-left text-sm font-semibold whitespace-nowrap text-zinc-100 w-min"
                ></th>
                <th
                  v-for="field in table"
                  scope="col"
                  class="py-3.5 pr-3 pl-4 text-left text-sm font-semibold whitespace-nowrap text-zinc-100 sm:pl-0"
                >
                  {{ field.name }}
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-zinc-800">
              <tr v-for="currency in currencies" :key="currency.id">
                <td class="text-sm whitespace-nowrap text-zinc-400 w-min">
                  {{ currency.sign }}
                </td>
                <td
                  v-for="field in table"
                  class="py-2 pr-3 pl-4 text-sm whitespace-nowrap text-zinc-400 sm:pl-0"
                >
                  {{ currency[field.field] }}
                </td>
                <td
                  class="w-full inline-flex items-center justify-end gap-x-3 py-2 text-sm font-medium whitespace-nowrap sm:pr-0"
                >
                  <button
                    @click="() => openEdit(currency)"
                    class="cursor-pointer text-cyan-200 hover:text-cyan-300"
                  >
                    Edit<span class="sr-only">, {{ currency.name }}</span>
                  </button>

                  <button
                    class="cursor-pointer text-red-600 hover:text-red-700"
                  >
                    Delete<span class="sr-only">, {{ currency.name }}</span>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  <ModalTemplate
    :modelValue="!!currentCurrency"
    @update:modelValue="currentCurrency = undefined"
  >
    <UserForm
      v-if="currentCurrency"
      @close="() => (currentCurrency = undefined)"
      @complete="(form) => onFinishEditing(form as unknown as Currency)"
      :form="currencyForm"
      :initial="(currentCurrency as any)"
      class="px-4 pb-4 pt-5 space-y-4 sm:p-6 sm:pb-4"
    >
      <div v-if="currentCurrencyError" class="mt-6 rounded-md bg-red-600/5 p-4 m-2">
        <div class="flex">
          <div class="flex-shrink-0">
            <XCircleIcon class="h-5 w-5 text-red-400" aria-hidden="true" />
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-bold text-red-400">
              {{ currentCurrencyError }}
            </h3>
          </div>
        </div>
      </div>
      <div
        class="rounded-b-lg bg-zinc-800 px-4 py-3 sm:flex sm:gap-x-2 sm:flex-row-reverse sm:px-6"
      >
        <LoadingButton
          type="submit"
          :loading="currentCurrencyLoading"
          :class="['inline-flex w-full shadow-sm sm:ml-3 sm:w-auto']"
        >
          Save
        </LoadingButton>
        <button
          type="button"
          class="cursor-pointer mt-3 inline-flex w-full justify-center rounded-md bg-zinc-900 px-3 py-2 text-sm font-semibold text-zinc-100 shadow-sm ring-1 ring-inset ring-zinc-700 hover:bg-zinc-950 sm:mt-0 sm:w-auto"
          @click="currentCurrency = undefined"
          ref="cancelButtonRef"
        >
          Cancel
        </button>
      </div>
    </UserForm>
  </ModalTemplate>
</template>

<script setup lang="ts">
import { XCircleIcon } from "@heroicons/vue/16/solid";
import { FetchError } from "ofetch";
import { CurrencyBacker, type Currency } from "~/types/currency";
import type { ServerResponse } from "~/types/error";
import { type UserForm } from "~/types/form";

definePageMeta({
  layout: "admin",
  auth: true,
});

useHead({
  title: "Currencies",
});

const createMode = ref(false);

const currentCurrency = ref<undefined | Currency>();
const currentCurrencyLoading = ref<boolean>(false);
const currentCurrencyError = ref<string | undefined>(undefined);

const materials = useMaterials();

function openCreate() {
  createMode.value = true;
  (currentCurrency.value as any) = {}
}

function openEdit(currency: Currency){
  createMode.value = false;
  currentCurrency.value = currency;
}

const currencyForm: UserForm = [
  {
    type: "text",
    name: "sign",
    title: "Symbol",
    helpText: "Single character notation",
    default: "",
    validate: (v) => v.length == 1,
    placeholder: "$",
    customClass: "col-span-2",
  },
  {
    type: "text",
    name: "identifier",
    title: "Identifier",
    helpText: "3 character notation.",
    validate: (v) => v.length == 3,
    default: "",
    placeholder: "USD",
    customClass: "col-span-2",
  },
  {
    type: "text",
    name: "name",
    title: "Name",
    helpText: "The long-form name of your currency.",
    default: "",
    placeholder: "United States Dollar",
    validate: (v) => !!v,
  },
  {
    type: "toggle",
    name: "canConvert",
    title: "Exchangable",
    helpText:
      "Whether or not this currency can be converted to and from other currencies",
    default: true,
  },
  {
    type: "number",
    name: "baseValue",
    title: "Value",
    helpText:
      "The conversion value of this currency. A higher number will equate to a higher value.",
    default: 1,
    validate: (v) => typeof v === "number" && !isNaN(v),
    enabled: (v) => !!v["canConvert"],
  },
  {
    type: "select",
    name: "backer",
    title: "Backer",
    helpText: "What this currency's value is tied to.",
    default: CurrencyBacker.Fiat,
    options: Object.keys(CurrencyBacker).filter((i) => isNaN(Number(i))),
  },
  {
    type: "searchable",
    name: "backingMaterial",
    title: "Material",
    helpText: "The material this currency is based on",
    default: "",
    options: materials.value,
    placeholder: "Select a backing material...",
    maxRender: 50,
    enabled: (v) => v["backer"] == CurrencyBacker.Material,
  },
];

async function onFinishEditing(currency: Currency) {
  try {
    if (!currentCurrency.value) return;

    currentCurrencyLoading.value = true;
    const currentCurrencyIndex = currencies.value.findIndex(
      (e) => e.id == currentCurrency.value?.id
    );
    const newCurrency = await useAPI<Currency>("/api/v1/currency", {
      method: createMode.value ? "POST" : "PATCH",
      body: {
        ...currency,
        id: createMode.value ? undefined : currentCurrency.value.id,
      },
    });
    if (createMode.value) {
      currencies.value.push(newCurrency);
    } else {
      Object.assign(currencies.value[currentCurrencyIndex], newCurrency);
    }
    currentCurrency.value = undefined;
  } catch (e: unknown) {
    if (e instanceof FetchError) {
      const data = e.data as ServerResponse;
      currentCurrencyError.value = `${data.message} (${data.status})`;
    } else {
      createModal(
        ModalType.Notification,
        {
          title: "Unknown error occurred",
          description: `An unknown error occurred while processing your request: ${e}`,
        },
        (_, c) => c()
      );
      throw e;
    }
  } finally {
    currentCurrencyLoading.value = false;
  }
}

const table: Array<{ name: string; field: keyof Currency }> = [
  {
    name: "ID",
    field: "identifier",
  },
  {
    name: "Name",
    field: "name",
  },
  {
    name: "Backer",
    field: "backer",
  },
  {
    name: "Material",
    field: "backingMaterial",
  },
  {
    name: "Value",
    field: "baseValue",
  },
  {
    name: "Exchangable",
    field: "canConvert",
  },
];

const currencies = ref(await useAPI<Array<Currency>>("/api/v1/currency"));
</script>
