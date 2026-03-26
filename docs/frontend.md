# 前端开发文档

## 1. 前端职责

前端主要负责：

- 页面展示与交互体验
- 路由管理
- 状态管理
- 接口请求与数据展示
- 登录态与权限控制

## 2. 推荐目录结构

```text
frontend/
├─ public/
├─ src/
│  ├─ api/
│  ├─ assets/
│  ├─ components/
│  ├─ layouts/
│  ├─ pages/
│  ├─ router/
│  ├─ store/
│  ├─ styles/
│  ├─ utils/
│  ├─ App.*
│  └─ main.*
├─ .env.development
├─ .env.production
└─ package.json
```

## 3. 技术方案建议

可按项目需要选择：

- React + Vite
- Vue 3 + Vite
- TypeScript
- Axios 或 Fetch 封装
- Zustand / Redux / Pinia 等状态管理方案

## 4. 页面开发规范

### 组件设计

- 优先拆分可复用组件
- 页面组件与基础组件职责分离
- 组件命名保持语义化

### 状态管理

- 页面私有状态放在页面内部
- 跨页面共享状态放在全局 store
- 接口缓存和请求状态建议统一管理

### 路由规范

- 按业务模块划分路由
- 区分公开页面与鉴权页面
- 动态路由统一做权限校验

## 5. API 调用规范

建议统一封装请求层：

```ts
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}
```

建议统一处理：

- 请求基础地址
- 超时时间
- Token 注入
- 响应拦截
- 错误提示

## 6. 环境变量建议

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_TITLE=System
```

说明：

- `VITE_API_BASE_URL`：后端接口基础地址
- `VITE_APP_TITLE`：前端应用名称

## 7. 联调建议

- 本地开发通过代理转发到后端
- 与后端约定统一返回结构
- 使用 Swagger/OpenAPI 或接口文档进行联调

## 8. 测试建议

- 单元测试：组件、工具函数
- 集成测试：页面交互、接口联动
- E2E 测试：核心业务流程

## 9. 发布建议

- 区分开发、测试、生产环境配置
- 打包后交由 Nginx 或静态资源服务托管
- 配置路由回退，避免刷新 404

## 10. 项目实际配置

### 10.1 当前技术栈

- **框架**: Vue 3.5.30
- **UI 组件**: Element Plus 2.10.2
- **状态管理**: Pinia 3.0.3
- **路由**: Vue Router 4.5.1
- **HTTP 客户端**: Axios 1.9.0
- **图表**: ECharts 5.6.0
- **图标**: @element-plus/icons-vue
- **构建工具**: Vite 8.0.1
- **TypeScript**: 5.9.3

### 10.2 核心依赖

```json
// package.json
{
  "dependencies": {
    "@element-plus/icons-vue": "^2.3.1",
    "axios": "^1.9.0",
    "echarts": "^5.6.0",
    "element-plus": "^2.10.2",
    "pinia": "^3.0.3",
    "vue": "^3.5.30",
    "vue-router": "^4.5.1"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^6.0.5",
    "typescript": "~5.9.3",
    "vite": "^8.0.1",
    "vue-tsc": "^3.2.5"
  }
}
```

### 10.3 Vite 配置

```typescript
// vite.config.ts
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
```

### 10.4 环境变量

**开发环境** (`.env.development`):
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_TITLE=药捷药品管理系统
```

**生产环境** (`.env.production`):
```env
VITE_API_BASE_URL=/api/v1
VITE_APP_TITLE=药捷药品管理系统
```

## 11. 启动指南

### 11.1 环境准备

1. **安装 Node.js 18+**
   ```bash
   node -v  # 验证版本
   npm -v   # 验证版本
   ```

2. **确保后端已启动**
   - 后端服务运行在 `http://localhost:8080`
   - 验证接口可用：http://localhost:8080/swagger-ui.html

### 11.2 安装依赖

首次运行需要安装依赖：

```bash
cd frontend
npm install
```

如果下载缓慢，可以使用淘宝镜像：

```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 11.3 启动开发服务器

```bash
# 方式一：直接启动
npm run dev

# 方式二：指定端口
npm run dev -- --port 5174

# 方式三：开放网络访问
npm run dev -- --host
```

### 11.4 验证启动成功

查看控制台输出：

```
VITE v8.0.1  ready in 1234 ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

访问前端页面：**http://localhost:5173**

### 11.5 构建生产版本

```bash
# 构建
npm run build

# 预览构建结果
npm run preview
```

构建产物在 `dist/` 目录，可部署到 Nginx 或其他静态服务器。

### 11.6 常见问题

**问题 1: 端口被占用**
```bash
# 修改 vite.config.ts 中的端口号
server: {
  port: 5174  # 改为其他端口
}
```

**问题 2: 跨域错误**
- 确认后端已启动且可访问
- 检查 vite.config.ts 中的代理配置
- 确认后端 CORS 配置正确

**问题 3: 依赖安装失败**
```bash
# 删除 node_modules 和锁文件
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

**问题 4: API 请求失败**
- 检查 `.env.development` 中的 `VITE_API_BASE_URL`
- 确认后端服务地址和端口
- 查看浏览器 Network 面板的详细信息

## 12. 页面清单

当前已实现的页面：

| 路径 | 页面名称 | 功能描述 |
|------|----------|----------|
| `/login` | 登录页 | 用户登录 |
| `/dashboard` | 首页 | 概览统计、最近预警 |
| `/medicine` | 药品管理 | 药品档案维护 |
| `/purchase` | 采购入库 | 采购单创建、入库 |
| `/sale` | 销售出库 | 销售单创建、出库 |
| `/inventory` | 库存管理 | 库存查询、批次管理 |
| `/warning` | 预警中心 | 预警列表、处理预警 |
| `/controlled` | 管制药品 | 特药管理、流水追溯 |
| `/stats` | 统计分析 | 数据报表、图表分析 |
| `/log` | 操作日志 | 审计日志查询 |
| `/user` | 用户管理 | 用户与角色管理 |

## 13. 待补充内容

- 页面清单
- 路由设计
- UI 规范
- 权限设计
- 状态管理方案
