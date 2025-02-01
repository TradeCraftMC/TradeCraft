import type { Currency } from "./currency";

export interface ItemStack {}

export interface LimitedDiscount {
    listing: Listing
    multiplier: number,
    ends: number
}

export interface Listing {
  vendor: Brand;
  costs: { [key: number]: [number, Currency] };
  stock: number;
  contents: Array<ItemStack>;
  discounts: Array<LimitedDiscount>;
}

export interface Brand {
  id: string;
  listings: Array<Listing>;
}
