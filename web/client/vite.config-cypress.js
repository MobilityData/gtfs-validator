import { sveltekit } from '@sveltejs/kit/vite';

/** @type {import('vite').UserConfig} */
const config = {
  plugins: [sveltekit()],
  optimizeDeps: {
    noDiscovery: true,
    include: [],
  },
  server: {
    expose: true,
    port: 5173,
    host: '127.0.0.1',
  },
};

export default config;