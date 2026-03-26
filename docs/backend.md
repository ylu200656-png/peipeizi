# 后端开发文档

## 1. 后端职责

后端主要负责：

- 提供业务接口
- 处理业务规则
- 管理数据库访问
- 提供用户认证与权限控制
- 输出日志、监控和异常信息

## 2. 推荐目录结构

```text
backend/
├─ src/
│  ├─ config/
│  ├─ controllers/
│  ├─ services/
│  ├─ repositories/
│  ├─ middlewares/
│  ├─ models/
│  ├─ routes/
│  ├─ utils/
│  └─ app.*
├─ tests/
├─ .env
└─ package.json / pom.xml / go.mod
```

## 3. 技术方案建议

可按团队熟悉度选择：

- Node.js: Express / NestJS / Fastify
- Java: Spring Boot
- Go: Gin / Fiber
- Python: FastAPI / Django

数据库可选：

- MySQL / PostgreSQL
- Redis 作为缓存

## 4. 分层设计建议

### Controller

- 接收请求
- 参数校验
- 调用 Service
- 返回统一响应

### Service

- 聚合业务逻辑
- 编排多个 Repository
- 保持核心规则集中管理

### Repository / DAO

- 封装数据库访问
- 避免业务逻辑直接写在数据访问层

## 5. 接口规范

### 路径建议

- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `POST /api/v1/users`
- `PUT /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

### 返回体建议

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

### 状态码建议

- `200`：请求成功
- `201`：创建成功
- `400`：参数错误
- `401`：未认证
- `403`：无权限
- `404`：资源不存在
- `500`：服务器错误

## 6. 安全建议

- 登录接口增加限流
- 密码必须加密存储
- Token 或 Session 管理统一化
- 敏感配置放入环境变量
- 所有写接口做权限校验

## 7. 环境变量建议

```env
PORT=8080
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=system
DB_USER=root
DB_PASSWORD=123456
REDIS_URL=redis://127.0.0.1:6379
JWT_SECRET=change_me
```

## 8. 数据库设计建议

至少补充以下内容：

- 用户表
- 角色表
- 权限表
- 业务主表
- 业务关联表
- 审计日志表

## 9. 测试建议

- 单元测试：服务逻辑
- 接口测试：路由与返回结构
- 集成测试：数据库和缓存协作

## 10. 部署建议

- 使用环境变量管理配置
- 提供健康检查接口，如 `/health`
- 接入日志与监控平台
- 生产环境启用反向代理与 HTTPS

## 11. 项目实际配置

### 11.1 当前技术栈

- **框架**: Spring Boot 3.4.4
- **安全**: Spring Security + JWT (jjwt 0.12.6)
- **ORM**: MyBatis 3.0.4
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **API 文档**: SpringDoc OpenAPI 2.8.6
- **构建工具**: Maven
- **Java 版本**: JDK 17

### 11.2 核心依赖

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 安全 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    
    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- API 文档 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.6</version>
    </dependency>
</dependencies>
```

## 12. 启动指南

### 12.1 环境准备

1. **安装 JDK 17**
   ```bash
   java -version  # 验证版本
   ```

2. **安装 Maven 3.8+**
   ```bash
   mvn -v  # 验证版本
   ```

3. **启动 MySQL 和 Redis**
   ```bash
   # MySQL 服务
   net start MySQL80
   
   # Redis 服务
   redis-server
   ```

### 12.2 配置文件

编辑 `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/yaojie?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456  # 修改为你的密码
  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

### 12.3 启动方式

#### 方式一：Maven 命令（推荐）

```bash
cd backend
mvn spring-boot:run
```

#### 方式二：IDEA 运行

1. 用 IDEA 打开 `backend` 目录
2. 找到 `YaojieBackendApplication.java`
3. 右键 -> Run 'YaojieBackendApplication'
4. 或点击绿色运行按钮

#### 方式三：打包后运行

```bash
cd backend
mvn clean package -DskipTests
java -jar target/yaojie-backend-0.0.1-SNAPSHOT.jar
```

### 12.4 验证启动成功

查看控制台输出：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.4.4)

Started YaojieBackendApplication in 5.123 seconds
Tomcat started on port(s): 8080 (http) with context path ''
```

访问以下地址验证：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 文档**: http://localhost:8080/api-docs
- **健康检查**: http://localhost:8080/actuator/health

### 12.5 常见问题

**问题 1: 端口被占用**
```bash
# 修改 application.yml 中的端口号
server:
  port: 8081  # 改为其他端口
```

**问题 2: 数据库连接失败**
- 检查 MySQL 服务是否启动
- 验证数据库名、用户名、密码是否正确
- 确认数据库 yaojie 已创建

**问题 3: Redis 连接失败**
```bash
# Windows 启动 Redis
redis-server

# 或使用 Windows 服务
net start Redis
```

## 13. 待补充内容

- 实体设计
- 数据库表结构
- 接口清单
- 权限模型
- 部署流程
