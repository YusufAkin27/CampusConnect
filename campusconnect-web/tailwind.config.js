/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Sora', 'system-ui', 'sans-serif'],
        display: ['Space Grotesk', 'system-ui', 'sans-serif'],
      },
      colors: {
        ink: '#0f172a',
        mist: '#f1f5f9',
        haze: '#e2e8f0',
        ocean: '#38bdf8',
      },
      boxShadow: {
        card: '0 20px 45px -30px rgba(15, 23, 42, 0.45)',
      },
    },
  },
  plugins: [],
}
