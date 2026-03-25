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

## 10. 待补充内容

- 页面清单
- 路由设计
- UI 规范
- 权限设计
- 状态管理方案
