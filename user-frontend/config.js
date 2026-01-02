// 后端接口的基础地址
const API_BASE_URL = "http://localhost:8080/api";

// 封装一个通用的请求函数，自动带上 Token
async function request(url, method = 'GET', data = null) {
    const token = localStorage.getItem('user_token');
    const headers = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = token;
    }

    const options = {
        method,
        headers,
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(API_BASE_URL + url, options);
        // 如果后端返回 401 (未授权)，说明 Token 过期，强制退回登录页
        if (response.status === 401) {
            alert("登录过期，请重新登录");
            window.location.href = 'login.html';
            return;
        }
        return await response.json();
    } catch (error) {
        console.error("请求出错:", error);
        alert("网络错误，请检查后端是否启动");
        return null;
    }
}