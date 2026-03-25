# 药捷药品管理系统

本仓库是“药捷”药品管理系统的开发基线仓库，当前阶段重点不是堆代码，而是先冻结架构、业务规则、接口契约、数据库规范和工程目录。

当前状态：

- 业务逻辑已完成一期梳理
- MySQL 建表与业务验证脚本已跑通
- Node / npm / Java / Maven 环境已完成验收
- 前后端工程目录已按后续开发需要提前设计
- 后端已完成 `Spring Boot + Maven` 初始化
- 前端已完成 `Vue 3 + Vite + TypeScript` 初始化
- 后端已完成统一错误码与异常处理
- 后端已完成 JWT 登录接口
- 后端已完成药品档案模块基础 CRUD
- 全栈统一规范已沉淀到架构基线文档

## 核心入口

- [全栈架构基线](C:/Users/30511/Desktop/System/doc/FULLSTACK_ARCHITECTURE.md)
- [文档索引](C:/Users/30511/Desktop/System/docs/README.md)
- [数据库建表脚本](C:/Users/30511/Desktop/System/sql/01_yaojie_schema.sql)
- [业务逻辑验证脚本](C:/Users/30511/Desktop/System/sql/02_business_logic_test.sql)
- [一键验证脚本](C:/Users/30511/Desktop/System/scripts/run-business-test.ps1)

## 当前技术基线

- 前端：`Vue 3 + Vite + TypeScript + Vue Router + Pinia + Axios + Element Plus + ECharts`
- 后端：`Java 17 + Spring Boot + Spring Security + Maven + MyBatis + MySQL + Redis`
- 数据库：`MySQL 8.0`
- 构建工具：`npm 10.8.2`、`Maven 4.0.0-rc-5`

## 当前目录

```text
System/
├─ backend/                  # 后端工程目录
├─ frontend/                 # 前端工程目录
├─ sql/                      # 建表、测试、种子数据脚本
├─ scripts/                  # 环境和业务验证脚本
├─ doc/                      # 统一架构基线文档
└─ docs/                     # 过程性分析与补充文档
```

## 开发顺序

1. 以 [FULLSTACK_ARCHITECTURE.md](C:/Users/30511/Desktop/System/doc/FULLSTACK_ARCHITECTURE.md) 为唯一开发基线。
2. 优先实现后端公共层、认证鉴权、药品档案、入库、销售、库存、预警。
3. 在后端主链路稳定后，再开始前端页面和联调。
4. 每次新增功能都先补数据库和接口契约，再写实现代码。

## 开发约束

- 库存按“药品 + 批次”管理
- 过期药品不计入可用库存
- 管制药品必须做权限加强
- 所有出入库和库存变动必须可追溯
- 文档优先于实现，若实现与基线冲突，以基线为准并先更新文档
## 2026-03-25 Progress Update

- 已完成登录、药品档案、采购入库三条主链路。
- 入库模块已打通前后端，前端页面位于 `frontend/src/views/purchase/index.vue`。
- 后端已提供 `GET /api/v1/purchases`、`GET /api/v1/purchases/{id}`、`POST /api/v1/purchases`。
- 入库服务已串联采购单、采购明细、库存累加、库存流水、预警刷新、操作日志。
- 已新增回归脚本 [run-purchase-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-purchase-smoke.ps1)，可一键验证“登录 -> 入库 -> 列表回查”。
## 2026-03-25 Sale And Inventory Update

- 已完成销售出库模块前后端联调。
- 已完成库存列表页联调，并修正“过期库存不计入可用库存”的口径。
- 后端新增 `GET /api/v1/inventories`、`GET /api/v1/inventories/available-batches`、`GET /api/v1/sales`、`GET /api/v1/sales/{id}`、`POST /api/v1/sales`。
- 已新增回归脚本 [run-sale-inventory-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-sale-inventory-smoke.ps1)，可验证“登录 -> 查询库存 -> 销售出库 -> 库存扣减”。
## 2026-03-25 Warning And Dashboard Update

- 已完成预警中心前后端联调。
- 已完成首页统计接口接真数据。
- 后端新增 `GET /api/v1/warnings`、`GET /api/v1/warnings/summary`、`GET /api/v1/stats/overview`。
- 首页已接入药品数、供应商数、库存批次数、可用库存总量、开放预警数、今日采购金额、今日销售金额。
- 已新增回归脚本 [run-warning-dashboard-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-warning-dashboard-smoke.ps1)，可验证预警汇总、预警列表和首页统计口径。
