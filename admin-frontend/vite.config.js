import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173, // 前端端口
    proxy: {
      '/api': { // 凡是遇到 /api 开头的请求，都代理到下面这个地址
        target: 'http://localhost:8080', // Java 后端地址
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '') // 注意：你的后端接口本身就有 /api 前缀，所以这里不需要 rewrite 去掉它
      }
    }
  }
})