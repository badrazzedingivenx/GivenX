/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'brand-cream': '#f4f1e8',
        'brand-green': '#1a4d2e',
      },
    },
  },
  plugins: [],
}
