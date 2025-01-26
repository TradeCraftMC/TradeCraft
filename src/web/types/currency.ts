export enum CurrencyBacker {
  Fiat = "Fiat",
  Material = "Material",
}

export interface Currency {
  id: string;
  identifier: string;
  name: string;
  sign: string;
  baseValue: number;
  canConvert: boolean;

  backer: CurrencyBacker;
  backingMaterial?: string;
}
