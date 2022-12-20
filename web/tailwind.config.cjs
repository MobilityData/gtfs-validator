/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {
      colors: {
        'mobi-dark-blue': '#170a2e',
        'mobi-purple': '#96a1ff', // poor contrast for text
        'mobi-purple-safe': '#7c88d8', // safe contrast ratio
        'mobi-light-blue': '#3859fa',
        'mobi-light-gray': '#f8f8f8',
      },

      container: {
        center: true,
        padding: {
          DEFAULT: '1rem',
          sm: '2rem',
          lg: '4rem',
          xl: '5rem',
          '2xl': '6rem',
        },
      },

      fontFamily: {
        sans: ['Mulish', 'Helvetica', 'sans-serif'],
        mono: ['IBM Plex Mono', 'SFMono-Regular', 'monospace'],
      },

      textColor: 'red',
    },
  },
  plugins: [],
};
