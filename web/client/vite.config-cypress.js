import { sveltekit } from '@sveltejs/kit/vite';

/** @type {import('vite').UserConfig} */
const config = {
  plugins: [sveltekit()],
  // optimizeDeps is necessary to avoid intermittent application reloads while testing.
  optimizeDeps: {
    noDiscovery: true,
    include: ['lodash', 'marked', 'svelte'],
  },
  server: {
    expose: true,
    port: 5173,
    // Host set to 127.0.0.1 instead of localhost to be able to run cypress on GitHub actions.
    host: '127.0.0.1',
  },
};

export default config;