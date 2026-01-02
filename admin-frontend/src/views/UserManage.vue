<template>
  <div class="manage-panel">
    <div class="header-bar">
      <h2>用户管理</h2>
      <div class="search-box">
        <input v-model="keyword" placeholder="输入用户名搜索..." />
        <button @click="handleSearch">搜索</button>
        <button @click="fetchUsers" class="reset-btn">重置</button>
      </div>
    </div>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>角色</th>
          <th>积分</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in userList" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>
            <span :class="user.role === 'ADMIN' ? 'tag-admin' : 'tag-user'">
              {{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </span>
          </td>
          <td>{{ user.points }}</td>
          <td>
            <button @click="resetPassword(user.id)" class="btn-warn">重置密码</button>
            <button @click="deleteUser(user.id)" class="btn-danger" v-if="user.role !== 'ADMIN'">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const userList = ref([]);
const keyword = ref('');

// 获取 Token 的辅助函数
const getToken = () => localStorage.getItem('token');

// 1. 获取所有用户
const fetchUsers = async () => {
  try {
    const res = await axios.get('/api/admin/users', {
      headers: { Authorization: getToken() } // 🚨 必须带 Token，否则后端拦截器会报 401
    });
    if (res.data.code === 200) {
      userList.value = res.data.data;
      keyword.value = ''; // 清空搜索框
    }
  } catch (err) {
    alert("获取用户列表失败，可能是 Token 过期，请重新登录");
  }
};

// 2. 搜索用户
const handleSearch = async () => {
  if (!keyword.value) return fetchUsers();
  try {
    const res = await axios.get(`/api/admin/users/search?keyword=${keyword.value}`, {
      headers: { Authorization: getToken() }
    });
    if (res.data.code === 200) userList.value = res.data.data;
  } catch (err) {
    console.error(err);
  }
};

// 3. 删除用户
const deleteUser = async (id) => {
  if (!confirm('确定要删除这个用户吗？')) return;
  try {
    const res = await axios.delete(`/api/admin/users/${id}`, {
      headers: { Authorization: getToken() }
    });
    if (res.data.code === 200) {
      alert('删除成功');
      fetchUsers(); // 刷新列表
    }
  } catch (err) {
    alert('删除失败');
  }
};

// 4. 重置密码
const resetPassword = async (id) => {
  if (!confirm('确定要将密码重置为 123456 吗？')) return;
  try {
    const res = await axios.post(`/api/admin/users/${id}/reset-password`, {}, {
      headers: { Authorization: getToken() }
    });
    if (res.data.code === 200) alert('密码已重置为 123456');
  } catch (err) {
    alert('操作失败');
  }
};

// 页面加载时自动获取数据
onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.manage-panel { padding: 20px; }
.header-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
th, td { padding: 12px; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #f8f9fa; font-weight: bold; }
button { padding: 6px 12px; cursor: pointer; border: none; border-radius: 4px; margin-right: 5px; color: white;}
.btn-warn { background-color: #ff9800; }
.btn-danger { background-color: #f44336; }
.search-box input { padding: 6px; border: 1px solid #ddd; margin-right: 5px; }
.search-box button { background-color: #2196F3; }
.search-box .reset-btn { background-color: #999; }
.tag-admin { color: #2196F3; font-weight: bold; }
.tag-user { color: #666; }
</style>