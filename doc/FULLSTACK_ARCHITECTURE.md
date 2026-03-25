# FULLSTACK_ARCHITECTURE

## 1. 文档定位

本文档是“药捷”药品管理系统的全栈开发基线文档。

目的只有一个：

让任何新的 AI 会话、任何新的开发者，在不重新梳理上下文的前提下，能够直接理解本项目的架构边界、业务规则、接口契约、数据库规范和标准开发流程。

当前阶段说明：

- 仓库已完成前后端工程初始化
- 当前文档描述的是“冻结后的开发基线 + 已落地的初始化骨架”
- 已落地统一错误码、全局异常、JWT 登录接口、药品档案模块基础 CRUD
- 后续实现必须严格遵守本文档

如果后续实现与本文档冲突，先更新本文档，再修改代码。

## 2. 项目目标

项目名称：药捷药品管理系统

项目定位：

- 面向中小型药店
- 面向社区卫生服务中心
- 面向基层药品管理场景

解决的问题：

- 药品档案分散
- 入库与销售流程不规范
- 库存风险无法及时发现
- 管制药品监管不足
- 缺少经营统计与追溯能力

一期目标：

- 完成药品建档
- 完成采购入库
- 完成销售出库
- 完成库存管理
- 完成低库存/临期/过期预警
- 完成管制药品权限加强
- 完成基础统计分析

## 3. 全栈技术图谱

### 3.1 前端技术栈

- `Vue 3`
- `Vite`
- `TypeScript`
- `Vue Router`
- `Pinia`
- `Axios`
- `Element Plus`
- `ECharts`

选择原则：

- Vue 3 负责页面与状态响应式实现
- Vite 负责构建与开发环境启动
- TypeScript 负责提升接口联调和类型约束稳定性
- Element Plus 负责后台管理类界面基础组件
- ECharts 负责库存和销售可视化

### 3.2 后端技术栈

- `Java 17`
- `Spring Boot`
- `Spring Security`
- `Maven`
- `MyBatis`
- `MySQL`
- `Redis`

选择原则：

- Spring Boot 负责应用启动与分层组织
- Spring Security 负责认证与鉴权
- MyBatis 负责数据访问控制
- MySQL 负责核心业务数据持久化
- Redis 负责登录态、热点缓存、预警辅助缓存

### 3.3 环境与构建工具

- Node.js：`v20.20.2`
- npm：`10.8.2`
- Java：`17.0.2`
- Maven：`4.0.0-rc-5`
- MySQL：`8.0.26`

### 3.4 部署基线

当前先定义单体部署架构，不做微服务拆分。

部署基线：

- 前端：Nginx 托管静态资源
- 后端：Spring Boot 单体服务
- 数据库：MySQL
- 缓存：Redis

## 4. 工程拓扑结构

### 4.1 根目录结构

```text
System/
├─ backend/
├─ frontend/
├─ sql/
├─ scripts/
├─ doc/
└─ docs/
```

职责说明：

- `backend/`：后端应用代码
- `frontend/`：前端应用代码
- `sql/`：数据库脚本与测试脚本
- `scripts/`：环境校验、业务验证、后续自动化脚本
- `doc/`：全局权威架构文档
- `docs/`：过程性分析与补充说明

### 4.2 前端目录结构

```text
frontend/
├─ public/
├─ src/
│  ├─ api/
│  │  └─ modules/
│  ├─ assets/
│  ├─ components/
│  │  ├─ base/
│  │  └─ business/
│  ├─ composables/
│  ├─ constants/
│  ├─ layouts/
│  ├─ router/
│  │  └─ modules/
│  ├─ store/
│  │  └─ modules/
│  ├─ styles/
│  ├─ types/
│  ├─ utils/
│  └─ views/
│     ├─ login/
│     ├─ dashboard/
│     ├─ medicine/
│     ├─ purchase/
│     ├─ sale/
│     ├─ inventory/
│     ├─ warning/
│     ├─ controlled/
│     ├─ stats/
│     ├─ user/
│     └─ log/
└─ README.md
```

边界原则：

- `api/` 只负责 HTTP 通信，不写页面逻辑
- `store/` 只负责状态，不直接访问 DOM
- `views/` 负责页面编排
- `components/business/` 放可复用业务组件
- `types/` 放接口 DTO/VO 映射类型

### 4.3 后端目录结构

```text
backend/
├─ src/main/java/com/yaojie/
│  ├─ common/
│  │  ├─ result/
│  │  ├─ exception/
│  │  ├─ enums/
│  │  └─ utils/
│  ├─ config/
│  │  ├─ swagger/
│  │  ├─ mybatis/
│  │  └─ redis/
│  ├─ modules/
│  │  ├─ auth/
│  │  ├─ system/
│  │  ├─ medicine/
│  │  ├─ purchase/
│  │  ├─ sale/
│  │  ├─ inventory/
│  │  ├─ warning/
│  │  ├─ stats/
│  │  └─ audit/
│  └─ security/
├─ src/main/resources/
│  ├─ mapper/
│  ├─ db/migration/
│  ├─ sql/seed/
│  └─ templates/
└─ src/test/java/com/yaojie/
   ├─ unit/
   └─ integration/
```

后端分层约束：

- `controller`：只接收请求与返回响应
- `service`：只写业务规则与事务编排
- `mapper`：只写数据库访问
- `entity`：数据库实体
- `dto`：入参对象
- `vo`：出参对象

组织原则：

- 采用“按业务模块分包”而不是“全局平铺 controller/service/mapper”
- 每个模块内部自带 controller/dto/entity/mapper/service/vo
- 公共能力只能进入 `common/`，不能把业务逻辑塞进公共层

## 5. 核心业务模块

一期业务模块固定如下：

- `auth`：登录认证与当前用户
- `system`：用户、角色、权限
- `medicine`：药品分类、供应商、药品档案
- `purchase`：采购入库
- `sale`：销售出库
- `inventory`：当前库存、库存流水、库存盘点
- `warning`：低库存、临期、过期预警
- `stats`：首页统计、销售趋势、预警统计
- `audit`：操作日志与审计留痕

## 6. 核心数据链路

一个标准请求生命周期如下：

1. 用户在前端页面触发动作，例如在“采购入库页”提交入库单。
2. 前端页面调用 `api/modules/purchase.ts`。
3. Axios 请求拦截器统一补充 Token、基础 URL、错误处理。
4. 请求进入后端 `PurchaseController`。
5. Spring Security 先校验认证状态和权限。
6. Controller 做参数接收和基础校验，然后调用 `PurchaseService`。
7. Service 在事务内执行业务：
   - 保存入库单主表
   - 保存入库明细
   - 更新批次库存
   - 写入库存流水
   - 刷新预警
   - 写入操作日志
8. Service 通过 MyBatis Mapper 与 MySQL 交互。
9. Service 返回业务结果给 Controller。
10. Controller 包装统一响应体后返回前端。
11. 前端响应拦截器统一处理业务码与异常提示。
12. 页面刷新列表、库存统计和预警状态。

## 7. API 交互契约

### 7.1 统一响应格式

所有业务接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

字段约定：

- `code = 0`：成功
- `code != 0`：业务失败
- `message`：给前端提示或日志定位用
- `data`：业务数据

### 7.2 HTTP 语义约定

- `GET`：查询
- `POST`：新增或执行性动作
- `PUT`：更新
- `DELETE`：删除或禁用

### 7.3 接口前缀

- 统一前缀：`/api/v1`

### 7.4 第一批接口

- `/api/v1/auth/*`
- `/api/v1/users/*`
- `/api/v1/roles/*`
- `/api/v1/categories/*`
- `/api/v1/suppliers/*`
- `/api/v1/medicines/*`
- `/api/v1/purchases/*`
- `/api/v1/sales/*`
- `/api/v1/inventories/*`
- `/api/v1/warnings/*`
- `/api/v1/stats/*`
- `/api/v1/operation-logs/*`

## 8. 认证与鉴权链路

### 8.1 认证方案

当前基线采用：

- `Spring Security + JWT`

原因：

- 适合前后端分离
- 后续前端联调简单
- 比 Session 更适合 API 化调用

### 8.2 前端约定

- 登录成功后保存 `accessToken`
- Token 存放在 Pinia + 本地持久化存储
- Axios 请求拦截器统一注入 `Authorization: Bearer <token>`

### 8.3 后端约定

- 登录接口签发 JWT
- Security 过滤器统一解析 Token
- 将当前登录用户放入安全上下文
- 基于角色和权限做接口拦截

当前已实现接口：

- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`

### 8.4 角色基线

- `ADMIN`
- `PHARMACY_MANAGER`
- `INVENTORY_MANAGER`
- `SALES_CLERK`

### 8.5 权限重点

- 管制药品入库：`ADMIN` / `PHARMACY_MANAGER` / `INVENTORY_MANAGER`
- 管制药品销售：`ADMIN` / `PHARMACY_MANAGER`
- 普通药品销售：允许 `SALES_CLERK`

## 9. 数据持久化规范

### 9.1 持久化框架

- `MyBatis`

原则：

- SQL 显式可控
- 适合库存、批次、预警这类强业务规则场景

### 9.2 事务规范

以下场景必须放在事务中：

- 入库
- 销售
- 库存盘点调整
- 用户角色绑定

事务要求：

- 单次业务操作要么全部成功，要么全部回滚
- 禁止跨多个 Service 手工拼装非原子事务

### 9.3 删除规范

一期默认策略：

- 主数据优先“状态禁用”，不直接物理删除
- 流水与单据不允许物理删除
- 审计数据只增不删

### 9.4 SQL 规范

- 所有主键统一使用 `BIGINT`
- 所有时间字段统一使用 `DATETIME`
- 所有金额统一使用 `DECIMAL`
- 所有库存数量统一使用 `INT`
- 所有枚举状态统一落库为固定值，不允许自由文本

## 10. 数据库基线

一期核心表：

- `sys_user`
- `sys_role`
- `sys_user_role`
- `medicine_category`
- `supplier`
- `medicine`
- `purchase_order`
- `purchase_order_item`
- `sale_order`
- `sale_order_item`
- `inventory`
- `inventory_record`
- `warning_record`
- `operation_log`

核心规则：

- 库存按“药品 + 批次”唯一约束
- 销售明细必须保存 `batch_no`
- 过期库存不算可用库存
- 库存变动必须写入 `inventory_record`

## 11. 业务规则基线

### 11.1 库存规则

- 入库增加库存
- 销售减少库存
- 库存不得为负
- 库存按批次管理

### 11.2 效期规则

- 入库必须记录生产日期和有效期
- 已过期药品禁止销售
- 临期药品触发预警

### 11.3 预警规则

- 可用库存小于等于安全库存时触发低库存预警
- 批次已过期时触发过期预警
- 批次临期时触发临期预警

### 11.4 管制药品规则

- `medicine.is_controlled = 1` 表示管制药品
- 管制药品的入库和销售必须额外鉴权
- 管制药品相关操作必须写操作日志

## 12. 当前已知风险与技术债

### 12.1 业务风险

- 目前销售是“指定批次销售”，尚未实现 FEFO 自动出库
- 多仓库未纳入一期范围
- 退货、撤销、逆向单据未纳入一期范围

### 12.2 技术风险

- 当前只完成工程初始化和基础骨架，核心业务模块尚未实现
- 当前只有数据库脚本验证，尚未完成完整 Java 层事务验证
- 当前药品档案模块已实现，但尚未补接口级自动化测试

### 12.3 安全风险

- 密码当前只在测试脚本中明文占位，正式开发必须改为加密存储
- JWT 过期策略和刷新策略尚未落地
- Redis 登录态黑名单机制尚未实现

## 13. 新增一个完整全栈功能的标准 SOP

以下流程必须固定执行，禁止跳步。

### 步骤 1：确认业务边界

- 明确功能是否属于现有模块
- 明确输入、输出、状态流转、权限规则
- 明确是否影响库存、预警、审计

### 步骤 2：更新架构文档

- 补充业务规则
- 补充接口清单
- 补充数据库变更点

### 步骤 3：设计数据库

- 新增或修改表结构
- 评审唯一约束、外键、索引
- 补充迁移脚本与测试种子数据

### 步骤 4：定义后端接口契约

- 定义 Controller 路由
- 定义 DTO、VO、错误码
- 定义权限要求

### 步骤 5：实现后端

- 写 Entity / DTO / VO
- 写 Mapper SQL
- 写 Service 事务逻辑
- 写 Controller
- 写全局异常与校验

### 步骤 6：实现前端

- 在 `types/` 增加接口类型
- 在 `api/modules/` 增加 API 封装
- 在 `store/modules/` 增加状态
- 在 `views/` 和 `components/` 完成页面实现

### 步骤 7：联调

- 先测成功路径
- 再测权限失败路径
- 再测边界异常路径
- 再测列表刷新和统计联动

### 步骤 8：补测试

- 后端单元测试
- 后端集成测试
- 数据库业务脚本回归
- 前端页面交互测试

### 步骤 9：补文档

- 更新 `FULLSTACK_ARCHITECTURE.md`
- 更新接口清单
- 更新数据库脚本说明

## 14. 下一步开发顺序

按以下顺序开始编码：

1. 完善统一响应体、异常体、认证框架
2. 落地药品档案模块
3. 落地采购入库模块
4. 落地销售出库模块
5. 落地库存与预警模块
6. 落地统计模块
7. 落地日志审计模块

## 15. 对后续 AI 的固定指令

后续新开 AI 会话时，第一句固定写：

请先阅读 `@doc/FULLSTACK_ARCHITECTURE.md`，然后严格按照其中的规范，帮我实现某个功能的前后端全链路代码。
## 2026-03-25 Delivery Update

Current delivered modules:

- Auth: JWT login, current-user query, route guard dependency ready.
- Medicine: archive query, create, update, category list, supplier list.
- Purchase: purchase create/list/detail, purchase-to-inventory flow completed.

Purchase transaction scope:

- Create `purchase_order`
- Create `purchase_order_item`
- Upsert batch inventory in `inventory`
- Insert inventory flow into `inventory_record`
- Refresh open warnings in `warning_record`
- Insert audit log into `operation_log`

Regression command:

- `powershell -ExecutionPolicy Bypass -File C:\Users\30511\Desktop\System\scripts\run-purchase-smoke.ps1`
## 2026-03-25 Sale And Inventory Update

Current delivered modules:

- Inventory: real inventory list query and sale batch option query.
- Sale: sale create/list/detail, outbound-to-inventory flow completed.

Sale transaction scope:

- Create `sale_order`
- Create `sale_order_item`
- Deduct batch inventory in `inventory`
- Insert inventory flow into `inventory_record`
- Refresh open warnings in `warning_record`
- Insert audit log into `operation_log`

Business rule clarification:

- Expired stock stays visible in inventory history, but it is not counted as available quantity.

Regression command:

- `powershell -ExecutionPolicy Bypass -File C:\Users\30511\Desktop\System\scripts\run-sale-inventory-smoke.ps1`
## 2026-03-25 Warning And Dashboard Update

Current delivered modules:

- Warning: warning list query and warning summary query.
- Stats: dashboard overview query with real database metrics.

Dashboard metrics:

- Active medicine count
- Active supplier count
- Inventory batch count
- Available stock total
- Open warning count
- Today purchase amount
- Today sale amount
- Latest open warnings

Regression command:

- `powershell -ExecutionPolicy Bypass -File C:\Users\30511\Desktop\System\scripts\run-warning-dashboard-smoke.ps1`
## 2026-03-25 Operation Log Update

Current delivered modules:

- Audit: operation log list query for admin and pharmacy manager.

Operation log query scope:

- Filter by `moduleName`
- Filter by `operationType`
- Return operator identity, business number, content, ip and created time

Regression command:

- `powershell -ExecutionPolicy Bypass -File C:\Users\30511\Desktop\System\scripts\run-operation-log-smoke.ps1`
