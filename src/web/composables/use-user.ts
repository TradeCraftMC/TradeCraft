export interface User {
  id: string;
  playerUUID: string;
}

export const useUser = () => useState<User | undefined>("user");
export const useSessionToken = () => useLocalState<string | undefined>("token");

export async function updateUser() {
  const token = useSessionToken();
  if (!token.value)
    throw createError({ statusCode: 403, statusMessage: "No session token" });
  const user = useUser();
  user.value = await $fetch<User>("/api/v1/auth/fetch", {
    headers: { Authorization: `Session ${token.value}` },
  });
}
