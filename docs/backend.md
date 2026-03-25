# 后端开发文档

## 1. 后端职责

后端主要负责：

- 提供业务接口
- 处理业务规则
- 管理数据库访问
- 提供用户认证与权限控制
- 输出日志、监控和异常信息

## 2. 推荐目录结构

```text
backend/
├─ src/
│  ├─ config/
│  ├─ controllers/
│  ├─ services/
│  ├─ repositories/
│  ├─ middlewares/
│  ├─ models/
│  ├─ routes/
│  ├─ utils/
│  └─ app.*
├─ tests/
├─ .env
└─ package.json / pom.xml / go.mod
```

## 3. 技术方案建议

可按团队熟悉度选择：

- Node.js: Express / NestJS / Fastify
- Java: Spring Boot
- Go: Gin / Fiber
- Python: FastAPI / Django

数据库可选：

- MySQL / PostgreSQL
- Redis 作为缓存

## 4. 分层设计建议

### Controller

- 接收请求
- 参数校验
- 调用 Service
- 返回统一响应

### Service

- 聚合业务逻辑
- 编排多个 Repository
- 保持核心规则集中管理

### Repository / DAO

- 封装数据库访问
- 避免业务逻辑直接写在数据访问层

## 5. 接口规范

### 路径建议

- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `POST /api/v1/users`
- `PUT /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

### 返回体建议

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

### 状态码建议

- `200`：请求成功
- `201`：创建成功
- `400`：参数错误
- `401`：未认证
- `403`：无权限
- `404`：资源不存在
- `500`：服务器错误

## 6. 安全建议

- 登录接口增加限流
- 密码必须加密存储
- Token 或 Session 管理统一化
- 敏感配置放入环境变量
- 所有写接口做权限校验

## 7. 环境变量建议

```env
PORT=8080
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=system
DB_USER=root
DB_PASSWORD=123456
REDIS_URL=redis://127.0.0.1:6379
JWT_SECRET=change_me
```

## 8. 数据库设计建议

至少补充以下内容：

- 用户表
- 角色表
- 权限表
- 业务主表
- 业务关联表
- 审计日志表

## 9. 测试建议

- 单元测试：服务逻辑
- 接口测试：路由与返回结构
- 集成测试：数据库和缓存协作

## 10. 部署建议

- 使用环境变量管理配置
- 提供健康检查接口，如 `/health`
- 接入日志与监控平台
- 生产环境启用反向代理与 HTTPS

## 11. 待补充内容

- 实体设计
- 数据库表结构
- 接口清单
- 权限模型
- 部署流程
