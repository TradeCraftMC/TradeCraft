import type { Currency } from "~/types/currency";

export const useCurrencies = () =>
  useState<Array<Currency>>("currencies", () => []);
export const useCurrentCurrency = () =>
  useState<string | undefined>("currency", () => undefined);
