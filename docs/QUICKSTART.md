# 快速启动指南

## 一键启动脚本（推荐）

### 方式一：使用自动化脚本（最简单）

**启动所有服务：**
```powershell
# 在项目根目录执行
.\scripts\start-all.ps1
```

脚本会自动：
- ✓ 检查环境依赖（Java、Maven、Node.js、MySQL、Redis）
- ✓ 检测端口占用（8080、5173）并自动清理
- ✓ 启动 MySQL 和 Redis 服务
- ✓ 后台启动后端和前端
- ✓ 显示访问地址和登录账号

**停止所有服务：**
```powershell
# 在项目根目录执行
.\scripts\stop-all.ps1
```

脚本会自动：
- ✓ 停止前后端进程
- ✓ 释放端口
- ✓ 清理后台作业

### 方式二：手动分步启动

### Windows PowerShell

**启动后端：**
```powershell
cd backend
mvn spring-boot:run
```

**启动前端：**
```powershell
cd frontend
npm run dev
```

## 分步启动指南

### 第一步：启动数据库和缓存

```powershell
# 启动 MySQL
net start MySQL80

# 启动 Redis
redis-server
# 或使用服务
net start Redis
```

### 第二步：初始化数据库（首次运行需要）

```powershell
# 创建数据库并导入数据
mysql -u root -p yaojie < sql/01_yaojie_schema.sql
mysql -u root -p yaojie < sql/02_business_logic_test.sql
mysql -u root -p yaojie < sql/03_warning_resolve_patch.sql
```

### 第三步：启动后端（端口 8080）

```powershell
# 方式一：Maven 运行（推荐）
cd backend
mvn spring-boot:run

# 方式二：打包后运行
cd backend
mvn clean package -DskipTests
java -jar target/yaojie-backend-0.0.1-SNAPSHOT.jar

# 方式三：IDEA 运行
# 打开 backend 目录到 IDEA
# 右键 YaojieBackendApplication.java -> Run
```

**验证后端启动成功：**
- 访问 Swagger UI: http://localhost:8080/swagger-ui.html
- 查看控制台输出 `Started YaojieBackendApplication`

### 第四步：启动前端（端口 5173）

```powershell
# 首次运行需要安装依赖
cd frontend
npm install

# 启动开发服务器
npm run dev
```

**验证前端启动成功：**
- 访问前端页面: http://localhost:5173
- 查看控制台输出 `VITE ready in XXXX ms`

## 默认登录账号

| 角色 | 用户名 | 密码 | 权限范围 |
|------|--------|------|----------|
| 管理员 | admin | admin123 | 全部权限 |
| 药房经理 | manager | manager123 | 药品、采购、销售、库存、预警、统计、日志、管制药品 |
| 库存管理员 | inventory | inventory123 | 药品、采购、库存、预警、管制药品 |
| 销售员 | sales | sales123 | 首页、统计、销售 |

## 技术栈总览

### 后端
- Spring Boot 3.4.4
- MyBatis 3.0.4
- MySQL 8.0
- Redis
- JWT 认证
- SpringDoc OpenAPI

### 前端
- Vue 3.5.30
- Element Plus 2.10.2
- Pinia 3.0.3
- Axios 1.9.0
- ECharts 5.6.0
- Vite 8.0.1

## 常用命令

### 后端命令
```powershell
# 运行测试
mvn test

# 打包
mvn clean package -DskipTests

# 查看依赖树
mvn dependency:tree

# 清理编译产物
mvn clean
```

### 前端命令
```powershell
# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview

# 运行代码检查
npm run lint
```

### 回归测试脚本
```powershell
# 业务测试
.\scripts\run-business-test.ps1

# 采购链路 Smoke 测试
.\scripts\run-purchase-smoke.ps1

# 销售库存 Smoke 测试
.\scripts\run-sale-inventory-smoke.ps1

# 预警处理 Smoke 测试
.\scripts\run-warning-resolve-smoke.ps1

# 用户角色 Smoke 测试
.\scripts\run-user-role-smoke.ps1
```

## 故障排查

### 后端启动失败

**问题 1: 端口 8080 被占用**
```powershell
# 查找占用端口的进程
netstat -ano | findstr :8080
taskkill /F /PID <进程 ID>

# 或修改 application.yml 中的端口号
server:
  port: 8081
```

**问题 2: MySQL 连接失败**
- 检查 MySQL 服务是否启动：`net start MySQL80`
- 验证数据库名、用户名、密码
- 确认数据库 yaojie 已创建

**问题 3: Redis 连接失败**
```powershell
# 启动 Redis
redis-server
# 或
net start Redis
```

### 前端启动失败

**问题 1: 端口 5173 被占用**
```bash
# 修改 vite.config.ts
server: {
  port: 5174
}
```

**问题 2: npm install 失败**
```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com
npm install
```

**问题 3: API 请求跨域错误**
- 确认后端已启动
- 检查 vite.config.ts 代理配置
- 清除浏览器缓存

## 开发环境建议

### IDEA 配置
1. 打开 `backend` 目录到 IDEA
2. 启用 Maven 自动导入
3. 配置代码格式化模板
4. 安装 Lombok 插件

### VS Code 配置
1. 安装 Volar (Vue 3) 插件
2. 安装 ESLint 插件
3. 安装 Prettier 插件
4. 配置保存时自动格式化

### Chrome 开发工具
- Vue DevTools
- Element Plus 开发者工具
- Redux DevTools (用于 Pinia)

## 下一步

启动成功后，你可以：

1. **体验完整业务流程**
   - 登录系统（使用 admin/admin123）
   - 创建药品档案
   - 创建采购入库单
   - 创建销售出库单
   - 查看库存变化
   - 处理预警记录

2. **查看 API 文档**
   - 访问 http://localhost:8080/swagger-ui.html
   - 测试各个接口

3. **运行自动化测试**
   - 执行 PowerShell 回归脚本
   - 验证业务功能正常

4. **开始二次开发**
   - 参考 `docs/architecture.md`
   - 查看 `doc/FULLSTACK_ARCHITECTURE.md`
   - 阅读 `docs/api-spec-v1.md`

## 相关文档

- [项目架构说明](./architecture.md)
- [后端开发文档](./backend.md)
- [前端开发文档](./frontend.md)
- [API 规范 v1](./api-spec-v1.md)
- [全栈架构基线](../doc/FULLSTACK_ARCHITECTURE.md)
