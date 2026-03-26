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

## 快速启动

📖 **详细启动指南**: [docs/QUICKSTART.md](docs/QUICKSTART.md)

### 一键启动（推荐）

```powershell
# 启动所有服务（自动检测环境、清理端口、启动前后端）
.\scripts\start-all.ps1

# 停止所有服务
.\scripts\stop-all.ps1
```

### 环境要求

- **Java**: JDK 17+
- **Node.js**: Node 18+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS yaojie DEFAULT CHARACTER SET utf8mb4;"

# 2. 导入表结构和数据
mysql -u root -p yaojie < sql/01_yaojie_schema.sql
mysql -u root -p yaojie < sql/02_business_logic_test.sql
mysql -u root -p yaojie < sql/03_warning_resolve_patch.sql
```

### 后端启动

```bash
# 方式一：使用 Maven 命令（推荐）
cd backend
mvn spring-boot:run

# 方式二：使用 IDEA 运行
# 1. 打开 backend 目录到 IDEA
# 2. 找到 YaojieBackendApplication.java
# 3. 右键 -> Run 'YaojieBackendApplication'

# 方式三：先打包再运行
cd backend
mvn clean package -DskipTests
java -jar target/yaojie-backend-0.0.1-SNAPSHOT.jar
```

**后端启动成功标志：**
```
Started YaojieBackendApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

访问 Swagger UI: http://localhost:8080/swagger-ui.html

### 前端启动

```bash
# 1. 安装依赖（首次运行需要）
cd frontend
npm install

# 2. 启动开发服务器
npm run dev

# 3. 构建生产版本
npm run build
```

**前端启动成功标志：**
```
  VITE v8.0.1  ready in XXXX ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

访问前端页面：http://localhost:5173

### 默认登录账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 药房经理 | manager | manager123 |
| 库存管理员 | inventory | inventory123 |
| 销售员 | sales | sales123 |

## 验证

- 后端：`mvn test`
- 前端：`npm run build`
- 回归脚本：
  - [run-warning-resolve-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-warning-resolve-smoke.ps1)
  - [run-controlled-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-controlled-smoke.ps1)
  - [run-stats-user-smoke.ps1](C:/Users/30511/Desktop/System/scripts/run-stats-user-smoke.ps1)

## 技术架构

### 后端技术栈

- **框架**: Spring Boot 3.4.4
- **安全**: Spring Security + JWT (jjwt 0.12.6)
- **ORM**: MyBatis 3.0.4
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **API 文档**: SpringDoc OpenAPI
- **构建工具**: Maven
- **Java 版本**: JDK 17

### 前端技术栈

- **框架**: Vue 3.5.30
- **UI 组件**: Element Plus 2.10.2
- **状态管理**: Pinia 3.0.3
- **路由**: Vue Router 4.5.1
- **HTTP 客户端**: Axios 1.9.0
- **图表**: ECharts 5.6.0
- **图标**: @element-plus/icons-vue
- **构建工具**: Vite 8.0.1
- **TypeScript**: 5.9.3

### 项目结构

```
System/
├── backend/                    # 后端工程
│   ├── src/main/java/com/yaojie/
│   │   ├── controller/        # 控制器层
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   ├── vo/                # 视图对象
│   │   ├── modules/           # 业务模块
│   │   │   ├── auth/         # 认证模块
│   │   │   ├── medicine/     # 药品管理
│   │   │   ├── purchase/     # 采购入库
│   │   │   ├── sale/         # 销售出库
│   │   │   ├── inventory/    # 库存管理
│   │   │   ├── warning/      # 预警中心
│   │   │   ├── controlled/   # 管制药品
│   │   │   ├── stats/        # 统计分析
│   │   │   ├── system/       # 系统管理
│   │   │   └── audit/        # 审计日志
│   │   ├── security/          # 安全配置
│   │   ├── config/            # 配置类
│   │   └── common/            # 公共组件
│   ├── src/main/resources/
│   │   ├── application.yml    # 配置文件
│   │   ├── sql/               # SQL 脚本
│   │   └── mapper/            # MyBatis XML
│   └── pom.xml                # Maven 配置
│
├── frontend/                   # 前端工程
│   ├── src/
│   │   ├── api/              # API 接口
│   │   ├── views/            # 页面组件
│   │   ├── components/       # 基础组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # 状态管理
│   │   ├── composables/      # 组合式函数
│   │   ├── utils/            # 工具函数
│   │   └── types/            # TypeScript 类型
│   ├── public/               # 静态资源
│   ├── package.json          # 依赖配置
│   └── vite.config.ts        # Vite 配置
│
├── sql/                      # 数据库脚本
│   ├── 01_yaojie_schema.sql
│   ├── 02_business_logic_test.sql
│   └── 03_warning_resolve_patch.sql
│
├── scripts/                  # PowerShell 脚本
│   ├── run-business-test.ps1
│   ├── run-purchase-smoke.ps1
│   ├── run-sale-inventory-smoke.ps1
│   └── ... (其他回归脚本)
│
└── docs/                     # 项目文档
    ├── README.md
    ├── architecture.md
    ├── backend.md
    ├── frontend.md
    └── api-spec-v1.md
```

## 权威文档

- [FULLSTACK_ARCHITECTURE.md](C:/Users/30511/Desktop/System/doc/FULLSTACK_ARCHITECTURE.md)
- [api-spec-v1.md](C:/Users/30511/Desktop/System/docs/api-spec-v1.md)
