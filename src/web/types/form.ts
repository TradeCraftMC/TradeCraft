export type InputType =
  | "text"
  | "password"
  | "number"
  | "toggle"
  | "select"
  | "searchable";

export type InputResultMap = {
  text: string;
  password: string;
  number: number;
  toggle: boolean;
  select: string;
  searchable: string;
};
export type InputResultUnion = InputResultMap[InputType];

export interface BaseInputOptions<T extends InputType> {
  name: string;
  type: T;
  default: InputResultMap[T];
  title: string;
  validate?: (value: InputResultMap[T]) => boolean;
  enabled?: (form: { [key: string]: InputResultUnion }) => boolean;
  helpText: string;
  placeholder?: string;
  customClass?: string;
}

export type CustomInputOptions = {
  text: {};
  password: {};
  number: {};
  toggle: {};
  select: { options: Array<string> };
  searchable: { options: Array<string>; maxRender: number };
};

export type TotalInputOptions<T extends InputType> = BaseInputOptions<T> &
  CustomInputOptions[T];

export type UserForm = Array<
  | TotalInputOptions<"text">
  | TotalInputOptions<"password">
  | TotalInputOptions<"number">
  | TotalInputOptions<"toggle">
  | TotalInputOptions<"select">
  | TotalInputOptions<"searchable">
>;
