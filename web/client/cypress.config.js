import { defineConfig } from "cypress";
// Base URL, API root and reports are set to 127.0.0.1 instead of localhost to be able to run cypress on GitHub actions.
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
    PUBLIC_CLIENT_API_ROOT: "http://127.0.0.1:8080",
    PUBLIC_CLIENT_REPORTS_ROOT: "http://127.0.0.1:8080/reports"
  },
});
