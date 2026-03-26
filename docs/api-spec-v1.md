# API Spec V1

## 统一响应

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 认证

### `POST /api/v1/auth/login`

- 说明：用户名密码登录
- 返回：`accessToken`、用户信息、角色列表

### `GET /api/v1/auth/me`

- 说明：获取当前登录用户

## 预警

### `GET /api/v1/warnings`

- 说明：查询预警列表
- 参数：
  - `warningType` 可选：`LOW_STOCK` / `EXPIRY_SOON` / `EXPIRED`
  - `status` 可选：`OPEN` / `RESOLVED`
- 权限：`ADMIN`、`PHARMACY_MANAGER`、`INVENTORY_MANAGER`

### `GET /api/v1/warnings/summary`

- 说明：获取预警汇总
- 权限：`ADMIN`、`PHARMACY_MANAGER`、`INVENTORY_MANAGER`

### `PUT /api/v1/warnings/{id}/resolve`

- 说明：手动处理预警
- 权限：`ADMIN`、`PHARMACY_MANAGER`、`INVENTORY_MANAGER`
- 请求体：

```json
{
  "handleRemark": "已电话确认并完成处理"
}
```

- 返回字段补充：
  - `handledBy`
  - `handlerName`
  - `handledAt`
  - `handleRemark`

- 失败场景：
  - 预警不存在：`4045`
  - 预警已处理：`4095`
  - 无权限：`4030`

## 用户管理

### `GET /api/v1/users`

- 说明：用户列表
- 权限：`ADMIN`

### `GET /api/v1/roles`

- 说明：角色列表
- 权限：`ADMIN`

### `POST /api/v1/users`

- 说明：新增用户
- 权限：`ADMIN`

### `PUT /api/v1/users/{id}/roles`

- 说明：分配角色
- 权限：`ADMIN`

### `PUT /api/v1/users/{id}/status`

- 说明：启停用户
- 权限：`ADMIN`

### `PUT /api/v1/users/{id}/reset-password`

- 说明：重置密码
- 权限：`ADMIN`

## 说明

- 当前阶段未引入 Playwright UI 自动化
- 当前阶段未开始库存流水页和盘点单接口
