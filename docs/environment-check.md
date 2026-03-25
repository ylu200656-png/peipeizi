# 环境验收记录

## 1. 检查时间

- 初次检查：`2026-03-25 22:08:48 +08:00`
- 修正后复检：`2026-03-25 22:14:15 +08:00`

## 2. 检查结论

当前环境已经修正为可直接命令行使用的状态：

- `Java`：可直接在命令行使用
- `Node.js / npm`：可直接在命令行使用
- `Maven`：可直接在命令行使用

这意味着：

- 后端 Java 运行环境是可用的
- 前端 Node 环境本体是可用的
- Maven 本体是可用的
- 当前用户级环境变量已修正完成

## 3. Java 检查结果

命令：

```powershell
java -version
```

结果：

- Java version: `17.0.2`
- Java runtime: `D:\Java\jdk-17.0.2`
- Java command path: `D:\Java\jdk-17.0.2\bin\java.exe`
- `JAVA_HOME`：`D:\Java\jdk-17.0.2`

结论：

- Java 环境正常
- 可以直接用于 Spring Boot / Maven 项目

## 4. Node.js 与 npm 检查结果

复检命令：

```powershell
node -v
npm -v
```

结果：

- Node.js: `v20.20.2`
- npm: `10.8.2`
- 安装目录：`C:\Program Files\nodejs`

结论：

- Node.js 和 npm 已安装成功
- `node` / `npm` 当前可直接执行
- 已补充用户级变量：`NODE_HOME=C:\Program Files\nodejs`
- 已补充用户级 `Path`：`C:\Program Files\nodejs`

## 5. Maven 检查结果

复检命令：

```powershell
mvn -version
```

结果：

- Apache Maven: `4.0.0-rc-5`
- Maven home: `C:\apache-maven-4.0.0-rc-5`
- Java version: `17.0.2`

结论：

- Maven 已安装并可直接使用
- 当前采用的全局 Maven 为 `C:\apache-maven-4.0.0-rc-5`
- 已补充用户级变量：
  - `MAVEN_HOME=C:\apache-maven-4.0.0-rc-5`
  - `M2_HOME=C:\apache-maven-4.0.0-rc-5`
- 已补充用户级 `Path`：`C:\apache-maven-4.0.0-rc-5\bin`

## 6. 本次修正内容

已执行的修正：

- 设置 `NODE_HOME=C:\Program Files\nodejs`
- 设置 `MAVEN_HOME=C:\apache-maven-4.0.0-rc-5`
- 设置 `M2_HOME=C:\apache-maven-4.0.0-rc-5`
- 用户级 `Path` 增加 `C:\Program Files\nodejs`
- 用户级 `Path` 保留 `C:\apache-maven-4.0.0-rc-5\bin`

说明：

- 旧的 `D:\nodejs` 不是有效 Node 安装目录
- 当前有效 Node 安装目录是 `C:\Program Files\nodejs`
- Maven 已统一到独立安装目录，不再依赖 IDEA 内置 Maven

## 7. 当前可执行命令

当前已经可以直接执行：

- `node -v`
- `npm -v`
- `mvn -version`
- `mvn clean package`
- `npm install`
- `npm run dev`

## 8. 对当前项目的判断

对“药捷”项目来说，当前环境判断如下：

- 后端 Java 版本满足开发要求
- Maven 命令行环境已可用
- 前端 Node.js 版本满足 Vue 3 开发要求
- npm 命令行环境已可用

因此目前最准确的结论是：

项目环境“已完成安装并已完成命令行配置”，可以进入前后端代码开发阶段。
