# frontend

前端工程已完成 `Vue 3 + Vite + TypeScript` 初始化，当前可以直接进入页面和联调开发。

## 启动命令

```powershell
npm install
npm run dev
```

## 构建命令

```powershell
npm run build
```

## 当前目标结构

```text
frontend/src
├─ api
│  └─ modules
├─ assets
├─ components
│  ├─ base
│  └─ business
├─ composables
├─ constants
├─ layouts
├─ router
│  └─ modules
├─ store
│  └─ modules
├─ styles
├─ types
├─ utils
└─ views
   ├─ login
   ├─ dashboard
   ├─ medicine
   ├─ purchase
   ├─ sale
   ├─ inventory
   ├─ warning
   ├─ controlled
   ├─ stats
   ├─ user
   └─ log
```

## 编码约束

- `api/modules` 只写接口封装
- `store/modules` 只写状态
- `views` 只负责页面编排
- `components/business` 放跨页面复用的业务组件
- `types` 统一放请求和响应类型定义

## 开发顺序

1. 登录页与基础布局
2. 路由与权限控制
3. 药品档案页
4. 入库页
5. 销售页
6. 库存页
7. 预警页
8. 统计页
9. 用户与日志页
