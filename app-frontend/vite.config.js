import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
// En Docker dev, le proxy cible app-backend (PROXY_API_TARGET / PROXY_WS_TARGET)
var apiTarget = process.env.PROXY_API_TARGET || 'http://localhost:8080';
var wsTarget = process.env.PROXY_WS_TARGET || 'ws://localhost:8080';
export default defineConfig({
    plugins: [react()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    server: {
        host: '0.0.0.0',
        port: 5173,
        proxy: {
            '/api': {
                target: apiTarget,
                changeOrigin: true,
                rewrite: function (p) { return p.replace(/^\/api/, ''); },
            },
            '/ws': {
                target: wsTarget,
                ws: true,
            },
        },
    },
});
