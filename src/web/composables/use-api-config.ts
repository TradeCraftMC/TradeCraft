export const useAPIConfig = () => useState<APIConfig>("api-config");

export type APIConfig = {
  appName: string;
};
