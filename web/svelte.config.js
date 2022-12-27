import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/kit/vite';

/** @type {import('@sveltejs/kit').Config} */
const config = {
  kit: {
    adapter: adapter({
      // fallback: '200.html',
      pages: 'build',
      trailingSlash: 'always'
    }),
  },
  preprocess: vitePreprocess(),
  prerender: { entries: [] },
};

export default config;
