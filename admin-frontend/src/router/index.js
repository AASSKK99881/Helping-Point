import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import UserManage from '../views/UserManage.vue'
import TaskManage from '../views/TaskManage.vue' // 1. 引入新页面

const routes = [
  { path: '/', name: 'Login', component: Login },
  { 
    path: '/home', 
    name: 'Home', 
    component: Home,
    redirect: '/home/users',
    children: [
      { path: 'users', component: UserManage },
      { path: 'tasks', component: TaskManage }  // 2. 这里使用的是真的组件了
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router