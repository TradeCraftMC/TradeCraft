import type { NitroFetchOptions, NitroFetchRequest } from "nitropack/types";

export async function useAPI<T>(
  request: NitroFetchRequest,
  opts?:
    | NitroFetchOptions<
        NitroFetchRequest,
        | "get"
        | "head"
        | "patch"
        | "post"
        | "put"
        | "delete"
        | "connect"
        | "options"
        | "trace"
      >
    | undefined
) {
  const sessionToken = useSessionToken();

  return $fetch<T>(request, {
    ...opts,
    headers: {
      ...opts?.headers,
      Authorization: `Session ${sessionToken.value}`,
    },
  });
}
