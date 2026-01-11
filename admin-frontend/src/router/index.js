import { createRouter, createWebHashHistory } from 'vue-router'
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

const
 router = createRouter({
  history
: createWebHashHistory(),
  routes
})

// 👇👇👇【开始插入代码：路由守卫】👇👇👇

router.beforeEach(
(to, from, next) =>
 {
  // 1. 获取 token (假设你登录成功后把 token 存到了 localStorage)
  // 注意：确认一下你的 Login.vue 里存的是叫 'token' 还是 'adminToken'？
  // 如果没改过，通常都叫 'token'。
  const token = localStorage.getItem('token'
); 

  // 2. 定义白名单 (不需要登录就能看的页面)
  const whiteList = ['/login', '/'
];

  // 3. 开始判断
  if
 (token) {
    // A. 如果有 token (说明已登录)
    if (to.path === '/login'
) {
      // 如果已登录还要去登录页，强制转到首页
      next({ 
path: '/home'
 });
    } 
else
 {
      // 去其他页面，放行
      next();
    }
  } 
else
 {
    // B. 如果没有 token (说明没登录)
    if
 (whiteList.includes(to.path)) {
      // 如果要去的是登录页，放行
      next();
    } 
else
 {
      // 如果要去后台其他页面，一脚踢回登录页
      next(
'/login'
);
    }
  }
});

// 👆👆👆【插入结束】👆👆👆

export default
 router