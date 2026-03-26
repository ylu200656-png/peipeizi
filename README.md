# 药捷药品管理系统

## 当前状态

当前仓库已经具备一期主业务链路，并完成了本阶段最小闭环：

- 前端权限裁剪补齐到菜单、路由、按钮级
- 预警处理闭环已落地
- 后端补了预警处理集成测试
- 新增预警处理 smoke 回归脚本

当前已落地能力：

- JWT 登录与当前用户查询
- 药品档案管理
- 采购入库
- 销售出库
- 库存查询
- 预警中心与预警处理
- 首页统计概览
- 统计分析页
- 管制药品专页
- 操作日志查询
- 用户与角色管理增强

## 当前约束

- 库存按“药品 + 批次”管理
- 过期库存可见但不可销售
- 管制药品必须加强授权和追溯
- 新增业务模块时，页面、接口、回归脚本至少形成一条闭环

## 本阶段新增

- `PUT /api/v1/warnings/{id}/resolve`
- 预警记录增加处理人、处理时间、处理备注
- 预警中心支持手动处理完成
- 菜单与路由按角色收口：
  - `ADMIN`：全部
  - `PHARMACY_MANAGER`：药品、采购、销售、库存、预警、统计、日志、管制药品
  - `INVENTORY_MANAGER`：药品、采购、库存、预警、管制药品
  - `SALES_CLERK`：首页、统计、销售

## 验证

- 后端：`mvn test`
- 前端：`npm run build`
- 回归脚本：
  - [run-warning-resolve-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-warning-resolve-smoke.ps1)
  - [run-controlled-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-controlled-smoke.ps1)
  - [run-stats-user-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-stats-user-smoke.ps1)

## 权威文档

- [FULLSTACK_ARCHITECTURE.md](C:/Users/30511/Desktop/System/doc/FULLSTACK_ARCHITECTURE.md)
- [api-spec-v1.md](C:/Users/30511/Desktop/System/docs/api-spec-v1.md)
