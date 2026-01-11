<template>
  <div class="app-layout">
    <aside class="sidebar">
      <div class="logo">校园互助平台</div>
      <nav>
        <router-link to="/home/users" active-class="active">用户管理</router-link>
        <router-link to="/home/tasks" active-class="active">求助帖管理</router-link>
      </nav>
      <div class="logout">
        <button @click="logout">退出登录</button>
      </div>
    </aside>

    <main class="content">
      <div class="top-bar">当前用户: 管理员</div>
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

const logout = () => {
  // 清除 Token 并跳转回登录页
  localStorage.removeItem('token');
  router.push('/');
};
</script>

<style scoped>
.app-layout { display: flex; height: 100vh; }
.sidebar { width: 220px; background-color: #2c3e50; color: white; display: flex; flex-direction: column; }
.logo { padding: 20px; font-size: 20px; font-weight: bold; text-align: center; border-bottom: 1px solid #34495e; }
nav { flex: 1; padding-top: 20px; }
nav a { display: block; padding: 15px 20px; color: #b8c7ce; text-decoration: none; transition: 0.3s; }
nav a:hover, nav a.active { background-color: #1abc9c; color: white; }
.logout { padding: 20px; border-top: 1px solid #34495e; text-align: center; }
.logout button { background: transparent; border: 1px solid #e74c3c; color: #e74c3c; padding: 5px 15px; cursor: pointer; border-radius: 4px; }
.logout button:hover { background: #e74c3c; color: white; }

.content { flex: 1; display: flex; flex-direction: column; background-color: #f0f2f5; }
.top-bar { height: 50px; background: white; display: flex; align-items: center; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
</style>