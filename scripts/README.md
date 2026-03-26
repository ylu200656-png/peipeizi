# PowerShell 启动/停止脚本使用说明

## 📋 脚本清单

| 脚本名称 | 功能 | 路径 |
|----------|------|------|
| 一键启动 | 自动检测环境、清理端口、启动前后端 | `scripts/start-all.ps1` |
| 一键停止 | 停止所有服务、释放端口 | `scripts/stop-all.ps1` |

---

## 🚀 start-all.ps1 - 一键启动脚本

### 功能特性

✅ **环境检查**
- 自动检测 Java、Maven、Node.js 是否安装
- 自动检查并启动 MySQL、Redis 服务
- 发现缺失会给出明确提示

✅ **端口管理**
- 自动检测端口 8080（后端）和 5173（前端）是否被占用
- 智能终止占用端口的进程
- 验证端口是否成功释放

✅ **服务启动**
- 后台启动后端（Maven Spring Boot）
- 后台启动前端（Vite Vue 3）
- 显示完整的启动信息和访问地址

✅ **用户友好**
- 彩色输出（成功/警告/错误不同颜色）
- 详细的进度提示
- 提供默认登录账号

### 使用方法

```powershell
# 方式一：在项目根目录执行
.\scripts\start-all.ps1

# 方式二：使用绝对路径
C:\Users\30511\Desktop\System\scripts\start-all.ps1

# 方式三：右键 -> 使用 PowerShell 运行
```

### 启动流程

1. **环境检查** - 验证所有依赖是否就绪
2. **端口检测** - 检查 8080 和 5173 端口
3. **清理端口** - 如有占用则终止相关进程
4. **启动数据库** - 确保 MySQL 和 Redis 运行
5. **启动后端** - Maven 启动 Spring Boot
6. **启动前端** - npm 启动 Vite
7. **显示信息** - 展示访问地址和账号

### 输出示例

```
========================================
   药捷药品管理系统 - 一键启动脚本
========================================

检查环境依赖...
  ✓ Java 已安装
  ✓ Maven 已安装
  ✓ Node.js 已安装 (v20.0.0)
  ✓ MySQL 服务正在运行
  ✓ Redis 服务正在运行

✓ 环境检查通过

========================================
   端口检测与清理
========================================

检查 后端 端口 8080...
✓ 端口 8080 可用

检查 前端 端口 5173...
⚠ 端口 5173 已被占用
检测到端口 5173 被占用，正在查找占用进程...
  找到进程：node (PID: 12345)
  正在终止进程...
  ✓ 端口 5173 已成功释放

========================================
   启动后端服务
========================================

正在启动后端（Maven Spring Boot）...
工作目录：C:\...\System\backend

✓ 后端启动命令已发送
  等待后端初始化完成（约 30-60 秒）...

========================================
   启动前端服务
========================================

正在启动前端（Vite Vue 3）...
工作目录：C:\...\System\frontend

✓ 前端启动命令已发送

========================================
   启动完成！
========================================

📌 服务访问地址:
   🌐 前端页面：http://localhost:5173
   📖 API 文档：http://localhost:8080/swagger-ui.html

🔑 默认登录账号:
   管理员：admin / admin123
   药房经理：manager / manager123
   库存管理员：inventory / inventory123
   销售员：sales / sales123

按任意键退出启动脚本（服务将继续在后台运行）...
```

---

## 🛑 stop-all.ps1 - 一键停止脚本

### 功能特性

✅ **智能停止**
- 自动查找占用端口的进程
- 优雅终止后端和前端服务
- 清理后台作业

✅ **端口释放**
- 验证进程是否完全停止
- 确认端口已释放
- 失败时给出处理建议

✅ **结果反馈**
- 显示每个服务的停止状态
- 失败时提供手动处理命令
- 彩色输出清晰明了

### 使用方法

```powershell
# 方式一：在项目根目录执行
.\scripts\stop-all.ps1

# 方式二：使用绝对路径
C:\Users\30511\Desktop\System\scripts\stop-all.ps1

# 方式三：右键 -> 使用 PowerShell 运行
```

### 停止流程

1. **停止后台作业** - 清理 Maven 和 npm 的后台进程
2. **停止后端** - 终止占用 8080 端口的进程
3. **停止前端** - 终止占用 5173 端口的进程
4. **验证结果** - 确认所有端口已释放

### 输出示例

```
========================================
   药捷药品管理系统 - 一键停止脚本
========================================

检查后台作业...
  停止作业：1 (mvn spring-boot:run)
  停止作业：2 (npm run dev)
  ✓ 后台作业已清理

========================================
   停止服务
========================================

停止 后端 (端口 8080)...
  找到进程：java (PID: 67890)
  正在终止进程...
  ✓ 后端已停止

停止 前端 (端口 5173)...
  找到进程：node (PID: 54321)
  正在终止进程...
  ✓ 前端已停止

========================================
   所有服务已成功停止！
========================================

✓ 后端端口 8080 已释放
✓ 前端端口 5173 已释放
```

---

## ⚙️ 配置说明

### 可修改的参数

在两个脚本的开头部分可以修改以下参数：

```powershell
# 配置参数
$BACKEND_PORT = 8080          # 后端端口
$FRONTEND_PORT = 5173         # 前端端口
$PROJECT_ROOT = Split-Path -Parent $PSScriptRoot  # 项目根目录
```

### 如需修改端口

如果要使用不同的端口号：

1. 打开 `scripts/start-all.ps1`
2. 修改 `$BACKEND_PORT` 和 `$FRONTEND_PORT`
3. 同样修改 `scripts/stop-all.ps1` 中的对应值

---

## 🔧 常见问题

### Q1: 脚本无法执行？

**解决方法：**
```powershell
# 查看当前执行策略
Get-ExecutionPolicy

# 如果显示 Restricted，需要修改策略
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned -Force
```

### Q2: 提示找不到 mvn 或 npm？

**原因：** 环境变量未配置

**解决方法：**
1. 将 Maven 的 bin 目录添加到 PATH
2. 确保 Node.js 正确安装

### Q3: 端口无法释放？

**手动处理方法：**
```powershell
# 查看占用端口的进程
netstat -ano | findstr :8080
netstat -ano | findstr :5173

# 强制终止进程
taskkill /F /PID <进程 ID>
```

### Q4: MySQL 或 Redis 启动失败？

**检查服务状态：**
```powershell
# 查看 MySQL 服务
Get-Service MySQL80

# 查看 Redis 服务
Get-Service Redis

# 手动启动服务
Start-Service MySQL80
Start-Service Redis
```

---

## 💡 最佳实践

### 日常开发流程

1. **上班启动**
   ```powershell
   .\scripts\start-all.ps1
   ```

2. **下班停止**
   ```powershell
   .\scripts\stop-all.ps1
   ```

3. **周末或长期离开**
   - 停止服务即可，脚本会自动清理
   - 无需手动关闭数据库（保持服务运行）

### 端口冲突处理

如果频繁遇到端口冲突：

1. 使用 `start-all.ps1` 自动清理
2. 或者修改默认端口号
3. 避免同时运行多个实例

### 性能优化

- 首次运行会下载 Maven 依赖和 npm 包，后续启动会很快
- 后台作业模式让启动更快速
- 自动跳过已运行的服务

---

## 📝 技术细节

### 端口检测原理

```powershell
# 使用 Get-NetTCPConnection 检测端口
$connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue

# 获取占用进程的 PID
$processId = $connection.OwningProcess

# 获取进程信息
$process = Get-Process -Id $processId
```

### 后台作业启动

```powershell
# 使用 Start-Job 创建后台作业
$job = Start-Job -ScriptBlock {
    Set-Location $using:PROJECT_ROOT\backend
    mvn spring-boot:run
}

# 查看作业状态
Get-Job -Id $job.Id

# 接收作业输出
Receive-Job -Id $job.Id
```

### 服务管理

```powershell
# 检查 Windows 服务
Get-Service -Name "MySQL*"
Get-Service -Name "Redis"

# 启动服务
Start-Service -Name "MySQL80"
Start-Service -Name "Redis"
```

---

## 🎯 总结

这两个脚本提供了：

- ✅ **零配置启动** - 自动处理所有前置条件
- ✅ **智能错误处理** - 清晰的错误提示和解决建议
- ✅ **完整的生命周期管理** - 从启动到停止的全流程
- ✅ **开发友好** - 彩色输出、详细日志、状态反馈

让开发和测试更加高效！🚀
