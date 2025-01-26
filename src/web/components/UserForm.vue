<template>
  <form @submit.prevent="() => completeForm()">
    <div :class="['gap-6 grid grid-cols-4', props.class]">
      <div
        v-for="item in props.form"
        :class="[item.customClass ?? 'col-span-4', 'relative']"
      >
        <div
          v-if="
            item.type == 'text' ||
            item.type == 'password' ||
            item.type == 'number'
          "
        >
          <label
            :for="item.name"
            class="block text-sm font-medium leading-6 text-zinc-100"
            >{{ item.title }}</label
          >
          <p
            :class="[
              item.validate !== undefined
                ? valids[item.name].value
                  ? 'text-cyan-200'
                  : 'text-red-500'
                : 'text-zinc-400',
              'block text-xs font-medium leading-6',
            ]"
          >
            {{ item.helpText }}
          </p>

          <div class="mt-2">
            <input
              :id="item.name"
              :name="item.name"
              :type="item.type"
              :autocomplete="item.name"
              :placeholder="item.placeholder"
              class="block w-full rounded-md border-0 py-1.5 px-3 bg-zinc-800 disabled:bg-zinc-900/80 text-zinc-100 disabled:text-zinc-400 shadow-sm ring-1 ring-inset ring-zinc-700 disabled:ring-zinc-800 placeholder:text-zinc-400 focus:ring-1 focus:ring-inset focus:ring-cyan-200 sm:text-sm sm:leading-6"
              :value="values[item.name].value"
              @input="
                                (v) =>
                                    setters[item.name](
                                        (v.target as HTMLInputElement)?.value,
                                    )
                            "
            />
          </div>
        </div>
        <SwitchGroup
          v-else-if="item.type == 'toggle'"
          as="div"
          class="flex items-center justify-between"
        >
          <span class="flex grow flex-col">
            <SwitchLabel
              as="span"
              class="text-sm/6 font-medium text-zinc-100"
              passive
              >{{ item.title }}</SwitchLabel
            >
            <SwitchDescription
              as="span"
              class="text-zinc-400 block text-xs font-medium leading-5"
              >{{ item.helpText }}</SwitchDescription
            >
          </span>
          <Switch
            :modelValue="values[item.name].value as boolean"
            @update:modelValue="
                            (v: boolean) => setters[item.name](v)
                        "
            :class="[
                            (values[item.name].value as boolean)
                                ? 'bg-cyan-200'
                                : 'bg-zinc-800',
                            'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:ring-2 focus:ring-cyan-200 focus:ring-offset-2 focus:outline-hidden',
                        ]"
          >
            <span
              aria-hidden="true"
              :class="[
                                (values[item.name].value as boolean)
                                    ? 'translate-x-5'
                                    : 'translate-x-0',
                                'pointer-events-none inline-block size-5 transform rounded-full bg-zinc-700 ring-0 shadow-sm transition duration-200 ease-in-out',
                            ]"
            />
          </Switch>
        </SwitchGroup>
        <Listbox
          as="div"
          v-else-if="item.type == 'select'"
          :modelValue="values[item.name].value"
          @update:modelValue="(v: string) => setters[item.name](v)"
        >
          <label
            :for="item.name"
            class="block text-sm font-medium leading-6 text-zinc-100"
            >{{ item.title }}</label
          >
          <p class="text-zinc-400 block text-xs font-medium leading-6">
            {{ item.helpText }}
          </p>
          <div class="relative mt-2">
            <ListboxButton
              class="grid w-full cursor-default grid-cols-1 rounded-md bg-zinc-800 py-1.5 pr-2 pl-3 text-left text-zinc-100 outline-1 -outline-offset-1 outline-zinc-700 focus:outline-2 focus:-outline-offset-2 focus:outline-cyan-200 sm:text-sm/6"
            >
              <span class="col-start-1 row-start-1 truncate pr-6">{{
                values[item.name].value
              }}</span>
              <ChevronUpDownIcon
                class="col-start-1 row-start-1 size-5 self-center justify-self-end text-zinc-400 sm:size-4"
                aria-hidden="true"
              />
            </ListboxButton>

            <transition
              leave-active-class="transition ease-in duration-100"
              leave-from-class="opacity-100"
              leave-to-class="opacity-0"
            >
              <ListboxOptions
                class="absolute z-10 mt-1 max-h-60 w-full overflow-auto rounded-md bg-zinc-800 py-1 text-base ring-1 shadow-lg ring-white/5 focus:outline-hidden sm:text-sm"
              >
                <ListboxOption
                  as="template"
                  v-for="option in item.options"
                  :key="option"
                  :value="option"
                  v-slot="{ active, selected }"
                >
                  <li
                    :class="[
                      active
                        ? 'bg-cyan-200 text-zinc-900 outline-hidden'
                        : 'text-zinc-100',
                      'relative cursor-default py-2 pr-9 pl-3 select-none',
                    ]"
                  >
                    <span
                      :class="[
                        selected ? 'font-semibold' : 'font-normal',
                        'block truncate',
                      ]"
                      >{{ option }}</span
                    >

                    <span
                      v-if="selected"
                      :class="[
                        active ? 'text-zinc-900' : 'text-zinc-100',
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
        <Combobox
          v-else-if="item.type == 'searchable'"
          as="div"
          :modelValue="values[item.name].value"
          @update:modelValue="(v: string) => setters[item.name](v)"
        >
          <label
            :for="item.name"
            class="block text-sm font-medium leading-6 text-zinc-100"
            >{{ item.title }}</label
          >
          <p class="text-zinc-400 block text-xs font-medium leading-6">
            {{ item.helpText }}
          </p>
          <div class="relative mt-2">
            <ComboboxInput
              class="block w-full rounded-md bg-zinc-800 py-1.5 pr-12 pl-3 text-base text-zinc-100 outline-1 -outline-offset-1 outline-zinc-700 placeholder:text-zinc-400 focus:outline-2 focus:-outline-offset-2 focus:outline-cyan-200 sm:text-sm/6"
              @change="searchables[item.name].query.value = $event.target.value"
              :placeholder="item.placeholder"
              :display-value="(v) => v as string"
            />
            <ComboboxButton
              class="absolute inset-y-0 right-0 flex items-center rounded-r-md px-2 focus:outline-hidden"
            >
              <ChevronUpDownIcon
                class="size-5 text-gray-400"
                aria-hidden="true"
              />
            </ComboboxButton>

            <ComboboxOptions
              v-if="searchables[item.name].filtered.value.length > 0"
              class="absolute z-10 mt-1 max-h-60 w-full overflow-auto rounded-md bg-zinc-800 py-1 text-base ring-1 shadow-lg ring-white/5 focus:outline-hidden sm:text-sm"
            >
              <ComboboxOption
                v-for="option in searchables[item.name].filtered.value"
                :key="option"
                :value="option"
                as="template"
                v-slot="{ active, selected }"
              >
                <li
                  :class="[
                    'relative cursor-default py-2 pr-9 pl-3 select-none',
                    active
                      ? 'bg-cyan-200 text-zinc-900 outline-hidden'
                      : 'text-zinc-100',
                  ]"
                >
                  <span
                    :class="['block truncate', selected && 'font-semibold']"
                  >
                    {{ option }}
                  </span>

                  <span
                    v-if="selected"
                    :class="[
                      'absolute inset-y-0 right-0 flex items-center pr-4',
                      active ? 'text-zinc-900' : 'text-zinc-100',
                    ]"
                  >
                    <CheckIcon class="size-5" aria-hidden="true" />
                  </span>
                </li>
              </ComboboxOption>
            </ComboboxOptions>
          </div>
        </Combobox>
        <div
          v-if="!enables[item.name].value"
          class="absolute inset-0 bg-zinc-900/60"
        />
      </div>
    </div>
    <slot />
  </form>
</template>

<script setup lang="ts">
import {
  type InputResultMap,
  type InputResultUnion,
  type InputType,
  type TotalInputOptions,
  type UserForm,
} from "~/types/form";
import {
  Switch,
  SwitchDescription,
  SwitchGroup,
  SwitchLabel,
  Listbox,
  ListboxButton,
  ListboxLabel,
  ListboxOption,
  ListboxOptions,
  Combobox,
  ComboboxButton,
  ComboboxInput,
  ComboboxLabel,
  ComboboxOption,
  ComboboxOptions,
} from "@headlessui/vue";
import { ChevronUpDownIcon } from "@heroicons/vue/16/solid";
import { CheckIcon } from "@heroicons/vue/20/solid";

const props = defineProps<{
  form: UserForm;
  class: string;
  initial: { [key: string]: InputResultUnion };
}>();

const emit = defineEmits<{
  (e: "complete", v: typeof completedForm.value): void;
}>();

const completedForm = computed(() =>
  Object.fromEntries(
    props.form.map((e) => [e.name, values[e.name].value]).filter((e) => e[1])
  )
);

function completeForm() {
  let failed = false;
  for (const field of props.form) {
    // Validate everything but setting it to itself
    setters[field.name](values[field.name].value);
    if (!valids[field.name].value) failed = true;
  }
  if (!failed) emit("complete", completedForm.value);
}

const index: { [key: string]: UserForm[0] } = Object.fromEntries(
  props.form.map((e) => [e.name, e])
);

const values: { [key: string]: Ref<InputResultUnion> } = Object.fromEntries(
  props.form.map((e) => {
    const dft =
      props.initial[e.name] && typeof props.initial[e.name] === typeof e.default
        ? props.initial[e.name]
        : e.default;

    return [e.name, ref(dft)];
  })
);

const valids: { [key: string]: Ref<boolean> } = Object.fromEntries(
  props.form.map((e) => [e.name, ref(true)])
);
const enables: { [key: string]: Ref<boolean> } = Object.fromEntries(
  props.form.map((e) => [e.name, ref(true)])
);
recalculateDisables();

const setters: {
  [key: string]: (v: InputResultUnion) => void;
} = Object.fromEntries(
  props.form.map((e) => [e.name, createVirtualValue(e.name)])
);

function recalculateDisables() {
  for (const field of props.form) {
    if (field.enabled === undefined) continue;
    const enabled = field.enabled(completedForm.value);
    enables[field.name].value = enabled;
  }
}

function cleanInput<T extends InputType = InputType>(
  v: InputResultUnion,
  type: T
): InputResultUnion {
  switch (type) {
    case "number":
      return parseFloat(v.toString());
    case "text":
    case "password":
    case "select":
      return v as string;
    case "toggle":
      return v as boolean;
    case "searchable":
      return v as string;
  }

  throw "";
}

function createVirtualValue<T extends InputType = InputType>(name: string) {
  return (v: InputResultMap[T]) => {
    const validator = index[name].validate as
      | ((v: InputResultUnion) => boolean)
      | undefined;
    const value = cleanInput(v, index[name].type);
    if (validator !== undefined) {
      const valid = validator(value);

      valids[name].value = valid;
    }

    values[name].value = value;
    recalculateDisables();
  };
}

const searchables: {
  [key: string]: { query: Ref<string>; filtered: ComputedRef<Array<string>> };
} = Object.fromEntries(
  props.form
    .filter((e) => e.type == "searchable")
    .map((e) => {
      const query = ref(values[e.name].value as string);
      return [
        e.name,
        {
          query,
          filtered: computed(() =>
            e.options
              .filter((e) =>
                e.toLowerCase().includes(query.value.toLowerCase())
              )
              .slice(0, e.maxRender)
          ),
        },
      ];
    })
);
</script>
