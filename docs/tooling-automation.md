# 工具与自动化约定

## 1. 文档定位

本文档说明当前仓库中的脚本、工具链、skills/MCP 使用边界，以及自动化的实际落地状态。
如果本文档与 [FULLSTACK_ARCHITECTURE.md](C:/Users/30511/Desktop/System/doc/FULLSTACK_ARCHITECTURE.md) 冲突，以代码现状为准，并同步修正文档。

## 2. 当前脚本体系

所有业务验证脚本位于 [scripts](C:/Users/30511/Desktop/System/scripts)。
公共能力统一收敛到 [TestHarness.ps1](C:/Users/30511/Desktop/System/scripts/lib/TestHarness.ps1)。

脚本与业务链对应关系如下：

- [run-business-test.ps1](C:/Users/30511/Desktop/System/scripts/run-business-test.ps1)：初始化数据库结构并执行基础业务逻辑 SQL 验证。
- [run-purchase-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-purchase-smoke.ps1)：验证登录、采购入库、采购列表回查。
- [run-sale-inventory-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-sale-inventory-smoke.ps1)：验证登录、库存查询、销售出库、库存回写。
- [run-warning-dashboard-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-warning-dashboard-smoke.ps1)：验证预警汇总、预警列表、首页统计概览。
- [run-operation-log-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-operation-log-smoke.ps1)：验证操作日志查询与筛选。
- [run-user-role-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-user-role-smoke.ps1)：验证用户列表、角色列表、角色分配。
- [run-controlled-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-controlled-smoke.ps1)：验证管制药品概览、批次、记录、预警、权限保护。
- [run-stats-user-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-stats-user-smoke.ps1)：验证统计分析接口和用户管理增强功能。

## 3. 脚本参数约定

所有 smoke 脚本优先读取参数，其次读取环境变量，最后回退到本地默认值。

统一支持的环境变量如下：

- `BASE_URL`：后端 API 前缀，默认 `http://127.0.0.1:8080/api/v1`
- `BACKEND_DIR`：后端工程目录，默认 `<repo>/backend`
- `MYSQL_EXE`：`mysql` 客户端路径，仅 `run-business-test.ps1` 使用
- `MYSQL_PASSWORD`：数据库 root 密码，仅 `run-business-test.ps1` 使用
- `TEST_USERNAME`：smoke 登录账号，默认 `admin`
- `TEST_PASSWORD`：smoke 登录密码，默认 `123456`

示例：

```powershell
$env:BASE_URL = "http://127.0.0.1:8080/api/v1"
$env:BACKEND_DIR = "C:\Users\30511\Desktop\System\backend"
$env:MYSQL_EXE = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$env:MYSQL_PASSWORD = "123456"
powershell -ExecutionPolicy Bypass -File .\scripts\run-business-test.ps1
```

## 4. 共享模块职责

[TestHarness.ps1](C:/Users/30511/Desktop/System/scripts/lib/TestHarness.ps1) 统一提供以下能力：

- 解析 `mvn` 命令路径
- 解析 `mysql` 命令路径
- 解析 smoke 默认配置
- 后端健康检查
- 按需启动和停止 Spring Boot 后端
- 获取 JWT 并组装认证请求头

新增 smoke 脚本时，必须复用该模块，不允许继续复制启动后端、登录鉴权、环境变量解析逻辑。

## 5. skills / MCP 状态

当前 skills 和 MCP 不是项目依赖，而是操作者本机能力。

已知本机能力包括：

- Codex 本地 skills：安装在 `C:\Users\30511\.codex\skills\`
- 会话级 MCP / browser / shell 工具：由当前代理运行环境提供

当前仓库中未落地以下项目级配置：

- `.mcp.json`
- Playwright 配置
- 项目内 agent 配置模板
- 可复现的 MCP 安装脚本

结论：

- 仓库开发和运行不依赖 skills/MCP
- skills/MCP 只能视为辅助开发能力，不能写进“项目必须依赖”
- 如果后续要团队化使用，需要补项目级安装说明或脚本

## 6. Redis / Docker 落地状态

### Redis

- 后端依赖和配置已声明
  - [pom.xml](C:/Users/30511/Desktop/System/backend/pom.xml)
  - [application.yml](C:/Users/30511/Desktop/System/backend/src/main/resources/application.yml)
- 当前业务代码未形成明确的 Redis 缓存、会话、限流或黑名单闭环

结论：Redis 目前属于“预留依赖，未正式落地”。

### Docker

- 当前仓库中没有 `Dockerfile`
- 当前仓库中没有 `docker-compose.yml`
- 当前开发、联调、回归均基于本机 Node / Java / Maven / MySQL 环境

结论：Docker 目前属于“规划中，未落地”。

## 7. 自动化约束

- 新增业务模块时，至少补一条可重复执行的验证脚本。
- 关键业务规则变更后，必须补回归脚本或扩充现有脚本。
- 脚本中禁止写死开发者用户名目录。
- 脚本中禁止继续新增明文路径常量，优先使用参数和环境变量。
- 文档必须明确哪些能力是“代码已落地”，哪些只是“规划/预留”。

## 8. 当前仍未完成的自动化工作

- 用 PowerShell 模块继续抽离更细的 HTTP 断言和 JSON 断言能力。
- 增加 CI 流水线，至少覆盖 `mvn test` 和 `npm run build`。
- 视需要补 Docker 化本地联调环境。
- 如果后续继续依赖 MCP/skills，补一份项目级可复现安装说明。
