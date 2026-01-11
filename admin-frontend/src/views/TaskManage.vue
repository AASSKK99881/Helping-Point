<template>
  <div class="manage-panel">
    <div class="header-bar">
      <h2>求助帖管理</h2>
      <div class="search-box">
        <input v-model="keyword" placeholder="输入标题或内容搜索..." />
        <button @click="handleSearch">搜索</button>
        <button @click="fetchTasks" class="reset-btn">重置</button>
      </div>
    </div>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>标题</th>
          <th>悬赏积分</th>
          <th>状态</th>
          <th>发布者ID</th>
          <th>发布时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in taskList" :key="task.id">
          <td>{{ task.id }}</td>
          <td>
            <div class="task-title">{{ task.title }}</div>
            <div class="task-content">{{ task.content }}</div>
          </td>
          <td class="points">{{ task.rewardPoints }}</td>
          <td>
            <span :class="'status-' + task.status.toLowerCase()">
              {{ formatStatus(task.status) }}
            </span>
          </td>
          <td>{{ task.publisherId }}</td>
          <td>{{ formatDate(task.createTime) }}</td>
          <td>
            <button @click="deleteTask(task.id)" class="btn-danger">
              删除
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const taskList = ref([]);
const keyword = ref("");
const getToken = () => localStorage.getItem("token");

// 状态翻译字典
const formatStatus = (status) => {
  const map = {
    PENDING: "待接单",
    ACCEPTED: "进行中",
    COMPLETED: "已完成",
  };
  return map[status] || status;
};

// 时间格式化
const formatDate = (timeStr) => {
  if (!timeStr) return "";
  return timeStr.replace("T", " ").substring(0, 19);
};

// 1. 获取所有帖子
const fetchTasks = async () => {
  try {
    const res = await axios.get("/api/tasks", {
      headers: { Authorization: getToken() },
    });
    if (res.data.code === 200) {
      taskList.value = res.data.data;
      keyword.value = "";
    }
  } catch (err) {
    console.error(err);
  }
};

// 2. 搜索帖子
const handleSearch = async () => {
  if (!keyword.value) return fetchTasks();
  try {
    const res = await axios.get(
      `/api/admin/tasks/search?keyword=${keyword.value}`,
      {
        headers: { Authorization: getToken() },
      }
    );
    if (res.data.code === 200) taskList.value = res.data.data;
  } catch (err) {
    console.error(err);
  }
};

// 3. 删除帖子
const deleteTask = async (id) => {
  if (!confirm("确定要删除这条求助吗？此操作不可恢复！")) return;
  try {
    const res = await axios.delete(`/api/tasks/${id}`, {
      headers: { Authorization: getToken() },
    });
    if (res.data.code === 200) {
      alert("删除成功");
      fetchTasks(); // 刷新列表
    }
  } catch (err) {
    alert("删除失败");
  }
};

onMounted(() => {
  fetchTasks();
});
</script>

<style scoped>
.manage-panel {
  padding: 20px;
}
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  table-layout: fixed;
}
th,
td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
  overflow: hidden;
  text-overflow: ellipsis;
}
th {
  background-color: #f8f9fa;
  font-weight: bold;
}

/* 列宽控制 */
th:nth-child(1) {
  width: 50px;
} /* ID */
th:nth-child(2) {
  width: 30%;
} /* 标题内容 */
th:nth-child(3) {
  width: 80px;
} /* 积分 */
th:nth-child(7) {
  width: 80px;
} /* 操作 */

.task-title {
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}
.task-content {
  font-size: 12px;
  color: #888;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.points {
  color: #f57c00;
  font-weight: bold;
}

/* 状态标签样式 */
.status-pending {
  color: #2196f3;
  background: #e3f2fd;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}
.status-accepted {
  color: #ff9800;
  background: #fff3e0;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}
.status-completed {
  color: #4caf50;
  background: #e8f5e9;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

button {
  padding: 6px 12px;
  cursor: pointer;
  border: none;
  border-radius: 4px;
  margin-right: 5px;
  color: white;
}
.btn-danger {
  background-color: #f44336;
}
.search-box input {
  padding: 6px;
  border: 1px solid #ddd;
  margin-right: 5px;
}
.search-box button {
  background-color: #2196f3;
}
.search-box .reset-btn {
  background-color: #999;
}
</style>