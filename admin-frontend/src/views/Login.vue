<template>
  <div class="login-container">
    <div class="login-box">
      <h2>管理员登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label>用户名</label>
          <input type="text" v-model="username" placeholder="请输入管理员账号" required />
        </div>
        <div class="input-group">
          <label>密码</label>
          <input type="password" v-model="password" placeholder="请输入密码" required />
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </form>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const errorMsg = ref('');
const loading = ref(false);
const router = useRouter();

const handleLogin = async () => {
  loading.value = true;
  errorMsg.value = '';

  try {
    // 发送登录请求
    // 因为配置了代理，这里的 /api 会自动转发到 localhost:8080/api
    const res = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    });

    if (res.data.code === 200) {
      // 1. 保存 Token 到本地
      localStorage.setItem('token', res.data.token);
      // 2. 跳转到管理主页 (暂时还没写，先预留)
      alert("登录成功！准备跳转...");
      router.push('/home'); 
    } else {
      errorMsg.value = res.data.msg || '登录失败';
    }
  } catch (err) {
    console.error(err);
    errorMsg.value = '网络错误或服务器未启动';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 350px;
}
h2 { text-align: center; margin-bottom: 20px; color: #333; }
.input-group { margin-bottom: 15px; }
.input-group label { display: block; margin-bottom: 5px; color: #666; }
.input-group input { 
  width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;
}
button {
  width: 100%; padding: 10px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px;
}
button:disabled { background-color: #ccc; }
.error { color: red; text-align: center; margin-top: 10px; }
</style>