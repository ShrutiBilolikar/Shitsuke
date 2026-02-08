/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        charcoal: '#1F2937',
        paper: '#F9FAFB',
        gold: '#EAB308',
        sage: '#10B981',
        crimson: '#EF4444',
        heatmap: {
          0: '#F3F4F6',
          25: '#DBEAFE',
          50: '#93C5FD',
          75: '#3B82F6',
          100: '#1E40AF',
        },
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
