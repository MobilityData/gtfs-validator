import adapter from '@sveltejs/adapter-static';
import { sveltekit } from '@sveltejs/kit/vite';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

// SvelteKit 3 reads its configuration from the sveltekit() plugin; svelte.config.js is no longer used.
/** @type {Parameters<typeof sveltekit>[0]} */
export const kitOptions = {
  adapter: adapter({
    // fallback: '200.html',
    pages: 'build',
  }),
  preprocess: vitePreprocess(),
};

/** @type {import('vite').UserConfig} */
const config = {
  plugins: [sveltekit(kitOptions)],
  server: {
    port: 5173,
  },
  optimizeDeps: {
    include: ['lodash', 'marked', 'svelte'],
  },
};

export default config;
