import { defineConfig } from "cypress";

export default defineConfig({
  watchForFileChanges: false,
  defaultCommandTimeout: 10000,
  video: true,
  e2e: {
    baseUrl: 'http://localhost:5173',
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  env: {
    PUBLIC_CLIENT_API_ROOT: "http://localhost:8080",
    PUBLIC_CLIENT_REPORTS_ROOT: "http://localhost:8080/reports"
  },
});
