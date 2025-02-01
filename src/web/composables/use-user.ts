import type { Brand } from "~/types/shop";

export interface User {
  id: string;
  name?: string;

  playerUUID: string;
  admin: boolean;

  // User's personal brand
  brand?: Brand;
}

export const useUser = () => useState<User | null | undefined>("user");
export const useSessionToken = () =>
  useLocalState<string | undefined>("token", () => undefined);

export async function updateUser() {
  const user = useUser();

  try {
    const token = useSessionToken();
    if (!token.value) {
      user.value = null;
      throw createError({ statusCode: 403, statusMessage: "No session token" });
    }

    user.value = await $fetch<User>("/api/v1/auth/fetch", {
      headers: { Authorization: `Session ${token.value}` },
    });
  } catch {
    user.value = null;
    throw createError({
      statusCode: 403,
      statusMessage: "Failed to authenticate",
    });
  }
}
