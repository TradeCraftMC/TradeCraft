export const useMaterials = () =>
  useState<Array<string>>("materials", () => []);

export async function loadMaterials() {
  const materails = useMaterials();
  materails.value = await $fetch("/api/v1/materials");
}
