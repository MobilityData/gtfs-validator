import { sveltekit } from '@sveltejs/kit/vite';

/** @type {import('vite').UserConfig} */
const config = {
  plugins: [sveltekit()],
  server: {
    expose: true,
    port: 5173,
  },
  optimizeDeps: {
    include: ['lodash', 'marked', 'svelte'],
  },
};

export default config;
