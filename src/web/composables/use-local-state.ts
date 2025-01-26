import { useState } from "#app";
/**
 * A wrapper for Nuxt3 `useState` which allows the data
 * to be saved in browser's `localStorage` as JSON.
 * The API is the same as `useState`.
 *
 * @param key A string key
 * @param defaultValue Optional default value of the key
 */
export function useLocalState<T>(key: string, defaultValue: () => T): Ref<T> {
  return computed({
    get() {
      const value = localStorage.getItem(key);
      if (!value) return defaultValue();
      const parsedValue = JSON.parse(value) as T;
      return parsedValue;
    },
    set(v) {
      localStorage.setItem(key, JSON.stringify(v));
    },
  });
}

export default useLocalState;
