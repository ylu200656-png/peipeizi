# 药捷药品管理系统

## 当前状态

当前仓库已经具备一期业务主链路，并且有可重复执行的 PowerShell smoke 脚本支撑联调与回归。

已落地能力：

- JWT 登录与当前用户查询
- 药品档案管理
- 采购入库
- 销售出库
- 库存查询
- 预警中心
- 首页统计概览
- 统计分析页接口
- 管制药品专页接口
- 操作日志查询
- 用户与角色管理增强

当前最重要的工程约束：

- 库存按“药品 + 批次”管理
- 过期库存可见但不可销售
- 管制药品必须走强化权限与追溯
- 新增业务模块时，页面、接口、回归脚本至少形成一条闭环

## 目录

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

## 权威文档

- [FULLSTACK_ARCHITECTURE.md](C:/Users/30511/Desktop/System/doc/FULLSTACK_ARCHITECTURE.md)
- [docs/README.md](C:/Users/30511/Desktop/System/docs/README.md)
- [docs/tooling-automation.md](C:/Users/30511/Desktop/System/docs/tooling-automation.md)
- [TOOLS_AUTOMATION.md](C:/Users/30511/Desktop/System/TOOLS_AUTOMATION.md)

## 验证脚本

- [run-business-test.ps1](C:/Users/30511/Desktop/System/scripts/run-business-test.ps1)
- [run-purchase-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-purchase-smoke.ps1)
- [run-sale-inventory-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-sale-inventory-smoke.ps1)
- [run-warning-dashboard-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-warning-dashboard-smoke.ps1)
- [run-operation-log-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-operation-log-smoke.ps1)
- [run-user-role-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-user-role-smoke.ps1)
- [run-controlled-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-controlled-smoke.ps1)
- [run-stats-user-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-stats-user-smoke.ps1)

## 技术栈

- 前端：`Vue 3 + Vite + TypeScript + Pinia + Axios + Element Plus + ECharts`
- 后端：`Java 17 + Spring Boot + Spring Security + Maven + MyBatis + MySQL`
- 预留依赖：`Redis`

说明：

- Redis 依赖和配置已声明，但业务闭环尚未正式落地。
- Docker 目前未落地，当前联调依赖本机 Node、Java、Maven、MySQL 环境。
- skills / MCP 为操作者本机能力，不是项目依赖。

## 下一步优先级

1. FEFO 自动出库
2. 盘点、调拨、退货
3. CI 与更强的自动化回归
