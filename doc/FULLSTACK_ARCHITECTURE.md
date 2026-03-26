# FULLSTACK_ARCHITECTURE

## 1. 文档定位

本文档是当前仓库的权威全栈基线。判断某项能力是否“已完成”，以“代码已落地 + 可重复验证”作为标准。

## 2. 当前已交付

- 认证登录：JWT 登录、当前用户查询
- 药品档案：列表、新增、编辑
- 采购入库：入库单创建、库存回写、预警刷新
- 销售出库：出库单创建、库存扣减、批次校验
- 库存查询：库存列表、可用批次
- 预警中心：列表、汇总、手动处理闭环
- 首页仪表盘：概览指标与最近预警
- 统计分析：趋势、库存分类、预警分布
- 管制药品：概览、列表、批次、流水、预警
- 用户管理：角色分配、新增用户、重置密码、启停用户
- 审计日志：操作日志查询

## 3. 本阶段新增收口

### 3.1 前端权限

前端权限当前做到：

- 菜单级
- 路由级
- 按钮级

当前权限口径：

- `ADMIN`：全部
- `PHARMACY_MANAGER`：药品、采购、销售、库存、预警、统计、日志、管制药品
- `INVENTORY_MANAGER`：药品、采购、库存、预警、管制药品
- `SALES_CLERK`：首页、统计、销售

### 3.2 预警处理闭环

预警处理采用“手动处理完成”方案，不支持重开。

规则：

- 仅 `OPEN` 预警可处理
- 处理后状态改为 `RESOLVED`
- 记录处理人、处理时间、处理备注
- 写入操作日志，模块固定 `WARNING`，操作类型固定 `RESOLVE`

## 4. 核心接口前缀

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

本阶段新增：

- `PUT /api/v1/warnings/{id}/resolve`

## 5. 工程约束

- 库存按“药品 + 批次”管理
- 过期库存可展示，但不计入可销售库存
- 管制药品必须保留库存批次、流水、预警和权限审计
- 权限裁剪以前后端统一角色边界为准

## 6. 自动化验证

### 6.1 当前已具备

- 后端集成测试：覆盖预警处理主链路
- 前端构建校验：`npm run build`
- smoke 回归脚本：位于 [scripts](C:/Users/30511/Desktop/System/scripts)

### 6.2 当前可执行脚本

- `run-business-test.ps1`
- `run-purchase-smoke.ps1`
- `run-sale-inventory-smoke.ps1`
- `run-warning-dashboard-smoke.ps1`
- `run-warning-resolve-smoke.ps1`
- `run-operation-log-smoke.ps1`
- `run-user-role-smoke.ps1`
- `run-controlled-smoke.ps1`
- `run-stats-user-smoke.ps1`

## 7. 暂未开始

- FEFO 自动出库
- 库存流水页面
- 盘点单闭环
- Playwright UI 自动化
- 调拨、退货、盘点审批流
