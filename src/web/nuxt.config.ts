// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2024-11-01",
  devtools: { enabled: true },
  vite: {
    server: {
      proxy: {
        "/api": "http://127.0.0.1:8080",
        "/assets": "http://127.0.0.1:8080",
      },
    },
  },
  app: {
    head: {
      link: [{ rel: "icon", href: "/assets/favicon.png" }],
    },
  },
  ssr: false,
});
