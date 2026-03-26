# FULLSTACK_ARCHITECTURE

## 1. 文档定位

本文档是“药捷药品管理系统”的当前全栈基线。

使用规则：

- 是否“完成交付”，以“页面或接口已落地 + 可重复执行的验证脚本存在”为准
- `docs/` 下文档只能补充说明，不能覆盖本文档
- 文档与代码冲突时，先核对代码，再同步修正文档

## 2. 当前交付状态

### 2.1 已落地

- 认证登录：JWT 登录、当前用户查询
- 药品档案：药品 CRUD、分类与供应商查询
- 采购入库：创建、列表、明细、库存回写、库存流水、预警刷新
- 销售出库：创建、列表、明细、批次校验、库存扣减
- 库存管理：库存列表、可用批次查询
- 预警中心：预警列表、预警汇总
- 首页仪表盘：真实统计指标、最近预警
- 统计分析：趋势、库存分类、预警分布
- 管制药品：概览、药品列表、批次、记录、预警
- 审计日志：操作日志查询与筛选
- 系统管理：用户列表、角色列表、角色分配、新增用户、重置密码、启停用户

### 2.2 已确认的关键规则

- 库存按“药品 + 批次”管理
- 过期库存保留展示，但不计入可销售库存
- 库存扣减必须在事务内完成
- 管制药品必须保留批次、流水、预警和权限审计
- 不能移除最后一个启用中的管理员

### 2.3 未完成

- FEFO 自动出库
- 盘点、调拨、退货闭环
- CI 流水线
- 更细粒度的自动化断言框架

## 3. 项目目标

项目面向中小型药店、社区卫生服务中心和基层药品管理场景。

一期目标：

- 药品建档
- 采购入库
- 销售出库
- 库存管理
- 低库存 / 临期 / 过期预警
- 管制药品监管
- 基础统计分析
- 基础系统管理

## 4. 技术栈

### 4.1 前端

- `Vue 3`
- `Vite`
- `TypeScript`
- `Vue Router`
- `Pinia`
- `Axios`
- `Element Plus`
- `ECharts`

### 4.2 后端

- `Java 17`
- `Spring Boot`
- `Spring Security`
- `Maven`
- `MyBatis`
- `MySQL`

### 4.3 预留但未完整落地

- `Redis`
  - 依赖和配置已存在
  - 业务缓存、限流、黑名单、会话闭环尚未正式落地
- `Docker`
  - 当前仓库中没有 Dockerfile 和 Compose 配置
  - 本地开发仍依赖本机环境

## 5. 工程结构

### 5.1 根目录

```text
System/
|-- backend/
|-- frontend/
|-- sql/
|-- scripts/
|-- doc/
|-- docs/
`-- TOOLS_AUTOMATION.md
```

### 5.2 前端目录

```text
frontend/
|-- public/
|-- src/
|   |-- api/
|   |   `-- modules/
|   |-- assets/
|   |-- components/
|   |   |-- base/
|   |   `-- business/
|   |-- layouts/
|   |-- router/
|   |   `-- modules/
|   |-- store/
|   |   `-- modules/
|   |-- styles/
|   |-- types/
|   `-- views/
|       |-- login/
|       |-- dashboard/
|       |-- medicine/
|       |-- purchase/
|       |-- sale/
|       |-- inventory/
|       |-- warning/
|       |-- controlled/
|       |-- stats/
|       |-- user/
|       `-- log/
`-- README.md
```

### 5.3 后端目录

```text
backend/src/main/java/com/yaojie/
|-- common/
|-- config/
|-- modules/
|   |-- auth/
|   |-- system/
|   |-- medicine/
|   |-- purchase/
|   |-- sale/
|   |-- inventory/
|   |-- warning/
|   |-- controlled/
|   |-- stats/
|   `-- audit/
`-- security/
```

## 6. 数据链路

标准链路：

1. 前端页面触发操作
2. `api/modules/*` 发起请求
3. Axios 注入 JWT
4. Spring Security 做认证与鉴权
5. Controller 接收参数
6. Service 在事务中处理业务
7. Mapper 访问 MySQL
8. 更新库存、预警、日志
9. 统一响应返回前端

当前已闭环的主链路：

- 登录 -> 药品档案
- 药品档案 -> 采购入库 -> 库存回写
- 库存 -> 销售出库 -> 库存回写
- 预警中心 -> 首页统计
- 统计分析 -> 趋势 / 分类 / 分布
- 系统管理 -> 用户创建 / 角色分配 / 重置密码 / 启停
- 管制药品 -> 批次 / 记录 / 预警查看

## 7. API 契约

统一响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

当前接口前缀：

- `/api/v1/auth/*`
- `/api/v1/users/*`
- `/api/v1/roles/*`
- `/api/v1/medicines/*`
- `/api/v1/purchases/*`
- `/api/v1/sales/*`
- `/api/v1/inventories/*`
- `/api/v1/warnings/*`
- `/api/v1/controlled/*`
- `/api/v1/stats/*`
- `/api/v1/operation-logs/*`

## 8. 认证与权限

- 登录成功后返回 JWT
- 前端保存 `accessToken`
- 请求统一注入 `Authorization: Bearer <token>`
- 页面访问由路由守卫控制
- 后端接口由 Spring Security 和 `@PreAuthorize` 控制

## 9. 验证脚本

所有验证脚本位于 [scripts](C:/Users/30511/Desktop/System/scripts)。

- `run-business-test.ps1`
- `run-purchase-smoke.ps1`
- `run-sale-inventory-smoke.ps1`
- `run-warning-dashboard-smoke.ps1`
- `run-operation-log-smoke.ps1`
- `run-user-role-smoke.ps1`
- `run-controlled-smoke.ps1`
- `run-stats-user-smoke.ps1`

要求：

- 新增业务模块时，至少补一条可重复执行的脚本
- 关键规则变更后，必须补回归
- 脚本公共逻辑统一复用 `scripts/lib/TestHarness.ps1`

## 10. 工具与自动化边界

- smoke 脚本是项目资产
- skills / MCP 是本机辅助能力，不是项目依赖
- Redis 当前是预留依赖，不应描述为“已完成缓存闭环”
- Docker 当前未落地，不应描述为“已容器化”

详细约定见：

- [tooling-automation.md](C:/Users/30511/Desktop/System/docs/tooling-automation.md)
- [TOOLS_AUTOMATION.md](C:/Users/30511/Desktop/System/TOOLS_AUTOMATION.md)

## 11. 后续顺序

建议按以下优先级推进：

1. FEFO 自动出库
2. 盘点、调拨、退货
3. CI 和更强的自动化回归
