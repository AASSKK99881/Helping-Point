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
            <button @click="openEdit(user)" class="btn-edit">修改</button>
            <button @click="resetPassword(user.id)" class="btn-warn">重置密码</button>
            <button @click="deleteUser(user.id)" class="btn-danger" v-if="user.role !== 'ADMIN'">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content">
        <h3>✏️ 修改用户信息</h3>
        <div class="form-group">
          <label>用户名:</label>
          <input v-model="editForm.username" type="text">
        </div>
        <div class="form-group">
          <label>积分:</label>
          <input v-model="editForm.points" type="number">
        </div>
        <div class="modal-actions">
          <button @click="closeEdit" class="btn-cancel">取消</button>
          <button @click="saveEdit" class="btn-save">保存</button>
        </div>
      </div>
    </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const userList = ref([]);
const keyword = ref('');
// 弹窗控制变量
const showEditModal = ref(false);
const editForm = ref({ id: null, username: '', points: 0 });

const getToken = () => localStorage.getItem('token');

// 1. 获取所有用户
const fetchUsers = async () => {
  try {
    const res = await axios.get('/api/admin/users', {
      headers: { Authorization: getToken() }
    });
    if (res.data.code === 200) {
      userList.value = res.data.data;
      keyword.value = '';
    }
  } catch (err) {
    alert("获取列表失败，请检查登录状态");
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

// 3. 打开修改弹窗
const openEdit = (user) => {
  editForm.value = { ...user }; // 复制当前行数据
  showEditModal.value = true;   // 显示弹窗
};

// 4. 关闭弹窗
const closeEdit = () => {
  showEditModal.value = false;
};

// 5. 保存修改 (调用后端接口)
const saveEdit = async () => {
  try {
    // 调用我们刚写的 AdminController 的 update 接口
    const res = await axios.put(`/api/admin/users/${editForm.value.id}`, editForm.value, {
      headers: { Authorization: getToken() }
    });
    
    if (res.data.code === 200) {
      alert("修改成功！");
      showEditModal.value = false;
      fetchUsers(); // 刷新列表
    } else {
      alert("修改失败：" + res.data.msg);
    }
  } catch (err) {
    alert("网络请求失败");
  }
};

const deleteUser = async (id) => {
  if (!confirm('确定要删除这个用户吗？')) return;
  try {
    const res = await axios.delete(`/api/admin/users/${id}`, {
      headers: { Authorization: getToken() }
    });
    if (res.data.code === 200) {
      alert('删除成功');
      fetchUsers();
    }
  } catch (err) {
    alert('删除失败');
  }
};

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

/* 按钮样式 */
button { padding: 6px 12px; cursor: pointer; border: none; border-radius: 4px; margin-right: 5px; color: white;}
.btn-edit { background-color: #2196F3; } /* 蓝色修改按钮 */
.btn-warn { background-color: #ff9800; }
.btn-danger { background-color: #f44336; }
.btn-save { background-color: #4CAF50; }
.btn-cancel { background-color: #999; }

.search-box input { padding: 6px; border: 1px solid #ddd; margin-right: 5px; }
.search-box button { background-color: #2196F3; }
.search-box .reset-btn { background-color: #999; }
.tag-admin { color: #2196F3; font-weight: bold; }
.tag-user { color: #666; }

/* --- 👇 弹窗样式 👇 --- */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5); /* 半透明遮罩 */
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
}
.modal-content {
  background: white; padding: 25px; border-radius: 8px; width: 300px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.form-group input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
</style>