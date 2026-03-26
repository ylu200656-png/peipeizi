# 药捷系统一键启动脚本
# 功能：自动检测并释放端口，一键启动前后端服务

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   药捷药品管理系统 - 一键启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 配置参数
$BACKEND_PORT = 8080
$FRONTEND_PORT = 5173
$PROJECT_ROOT = Split-Path -Parent $PSScriptRoot

# 颜色定义
$SuccessColor = "Green"
$WarningColor = "Yellow"
$ErrorColor = "Red"
$InfoColor = "Cyan"

# 函数：检查端口是否被占用
function Test-PortInUse {
    param (
        [int]$Port
    )
    
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $null -ne $connection
}

# 函数：获取占用端口的进程 ID
function Get-ProcessByPort {
    param (
        [int]$Port
    )
    
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    if ($connection) {
        return $connection.OwningProcess
    }
    return $null
}

# 函数：终止占用端口的进程
function Stop-ProcessByPort {
    param (
        [int]$Port
    )
    
    Write-Host "检测到端口 $Port 被占用，正在查找占用进程..." -ForegroundColor $WarningColor
    
    $processId = Get-ProcessByPort -Port $Port
    
    if ($processId) {
        try {
            $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
            if ($process) {
                Write-Host "  找到进程：$($process.Name) (PID: $processId)" -ForegroundColor $WarningColor
                Write-Host "  正在终止进程..." -ForegroundColor $WarningColor
                Stop-Process -Id $processId -Force
                Start-Sleep -Seconds 1
                
                # 验证端口是否已释放
                if (-not (Test-PortInUse -Port $Port)) {
                    Write-Host "  ✓ 端口 $Port 已成功释放" -ForegroundColor $SuccessColor
                    return $true
                } else {
                    Write-Host "  ✗ 端口 $Port 仍然被占用" -ForegroundColor $ErrorColor
                    return $false
                }
            }
        } catch {
            Write-Host "  ✗ 终止进程失败：$_" -ForegroundColor $ErrorColor
            return $false
        }
    } else {
        Write-Host "  未找到占用进程" -ForegroundColor $WarningColor
        return $false
    }
}

# 函数：检查并释放端口
function Confirm-PortAvailable {
    param (
        [int]$Port,
        [string]$ServiceName
    )
    
    Write-Host "检查 $ServiceName 端口 $Port..." -ForegroundColor $InfoColor
    
    if (Test-PortInUse -Port $Port) {
        Write-Host "⚠ 端口 $Port 已被占用" -ForegroundColor $WarningColor
        
        $result = Stop-ProcessByPort -Port $Port
        
        if (-not $result) {
            Write-Host "✗ 无法释放端口 $Port，请手动处理后重试" -ForegroundColor $ErrorColor
            Write-Host "  可以使用以下命令查看占用进程：" -ForegroundColor $WarningColor
            Write-Host "  netstat -ano | findstr :$Port" -ForegroundColor $Gray
            return $false
        }
    } else {
        Write-Host "✓ 端口 $Port 可用" -ForegroundColor $SuccessColor
    }
    
    return $true
}

# 函数：检查环境依赖
function Test-Environment {
    Write-Host "检查环境依赖..." -ForegroundColor $InfoColor
    
    $hasError = $false
    
    # 检查 Java
    try {
        $javaVersion = java -version 2>&1
        Write-Host "  ✓ Java 已安装" -ForegroundColor $SuccessColor
    } catch {
        Write-Host "  ✗ Java 未安装或未配置环境变量" -ForegroundColor $ErrorColor
        $hasError = $true
    }
    
    # 检查 Maven
    try {
        $mavenVersion = mvn -version 2>&1
        Write-Host "  ✓ Maven 已安装" -ForegroundColor $SuccessColor
    } catch {
        Write-Host "  ✗ Maven 未安装或未配置环境变量" -ForegroundColor $ErrorColor
        $hasError = $true
    }
    
    # 检查 Node.js
    try {
        $nodeVersion = node -version 2>&1
        Write-Host "  ✓ Node.js 已安装 ($nodeVersion)" -ForegroundColor $SuccessColor
    } catch {
        Write-Host "  ✗ Node.js 未安装或未配置环境变量" -ForegroundColor $ErrorColor
        $hasError = $true
    }
    
    # 检查 MySQL
    try {
        $mysqlService = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue
        if ($mysqlService -and $mysqlService.Status -eq 'Running') {
            Write-Host "  ✓ MySQL 服务正在运行" -ForegroundColor $SuccessColor
        } else {
            Write-Host "  ⚠ MySQL 服务未运行，尝试启动..." -ForegroundColor $WarningColor
            Start-Service -Name "MySQL80" -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 2
            
            $mysqlService = Get-Service -Name "MySQL80" -ErrorAction SilentlyContinue
            if ($mysqlService -and $mysqlService.Status -eq 'Running') {
                Write-Host "  ✓ MySQL 服务已启动" -ForegroundColor $SuccessColor
            } else {
                Write-Host "  ✗ MySQL 服务启动失败，请手动启动" -ForegroundColor $ErrorColor
                $hasError = $true
            }
        }
    } catch {
        Write-Host "  ✗ 无法检测 MySQL 服务" -ForegroundColor $ErrorColor
        $hasError = $true
    }
    
    # 检查 Redis
    try {
        $redisService = Get-Service -Name "Redis" -ErrorAction SilentlyContinue
        if ($redisService -and $redisService.Status -eq 'Running') {
            Write-Host "  ✓ Redis 服务正在运行" -ForegroundColor $SuccessColor
        } else {
            Write-Host "  ⚠ Redis 服务未运行，尝试启动..." -ForegroundColor $WarningColor
            Start-Service -Name "Redis" -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 2
            
            $redisService = Get-Service -Name "Redis" -ErrorAction SilentlyContinue
            if ($redisService -and $redisService.Status -eq 'Running') {
                Write-Host "  ✓ Redis 服务已启动" -ForegroundColor $SuccessColor
            } else {
                Write-Host "  ✗ Redis 服务启动失败，请手动启动" -ForegroundColor $ErrorColor
                $hasError = $true
            }
        }
    } catch {
        Write-Host "  ✗ 无法检测 Redis 服务" -ForegroundColor $ErrorColor
        $hasError = $true
    }
    
    Write-Host ""
    
    if ($hasError) {
        Write-Host "✗ 环境检查失败，请先解决上述问题" -ForegroundColor $ErrorColor
        return $false
    } else {
        Write-Host "✓ 环境检查通过" -ForegroundColor $SuccessColor
        return $true
    }
}

# 主流程
Write-Host ""

# 1. 环境检查
if (-not (Test-Environment)) {
    Write-Host ""
    Write-Host "请按提示安装并配置所需环境后重试" -ForegroundColor $ErrorColor
    pause
    exit 1
}

# 2. 检查并释放端口
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   端口检测与清理" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$backendPortAvailable = Confirm-PortAvailable -Port $BACKEND_PORT -ServiceName "后端"
$frontendPortAvailable = Confirm-PortAvailable -Port $FRONTEND_PORT -ServiceName "前端"

Write-Host ""

if (-not $backendPortAvailable -or -not $frontendPortAvailable) {
    Write-Host "✗ 端口清理失败，无法继续启动" -ForegroundColor $ErrorColor
    pause
    exit 1
}

# 3. 启动后端
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   启动后端服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "$PROJECT_ROOT\backend"

Write-Host "正在启动后端（Maven Spring Boot）..." -ForegroundColor $InfoColor
Write-Host "工作目录：$PROJECT_ROOT\backend" -ForegroundColor $Gray
Write-Host ""

# 使用后台作业启动后端
$backendJob = Start-Job -ScriptBlock {
    Set-Location $using:PROJECT_ROOT\backend
    mvn spring-boot:run
}

Write-Host "✓ 后端启动命令已发送" -ForegroundColor $SuccessColor
Write-Host "  等待后端初始化完成（约 30-60 秒）..." -ForegroundColor $InfoColor
Write-Host ""

# 等待后端启动
Start-Sleep -Seconds 5

# 4. 启动前端
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   启动前端服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "$PROJECT_ROOT\frontend"

Write-Host "正在启动前端（Vite Vue 3）..." -ForegroundColor $InfoColor
Write-Host "工作目录：$PROJECT_ROOT\frontend" -ForegroundColor $Gray
Write-Host ""

# 使用后台作业启动前端
$frontendJob = Start-Job -ScriptBlock {
    Set-Location $using:PROJECT_ROOT\frontend
    npm run dev
}

Write-Host "✓ 前端启动命令已发送" -ForegroundColor $SuccessColor
Write-Host ""

# 5. 显示启动信息
Start-Sleep -Seconds 3

Write-Host "========================================" -ForegroundColor Green
Write-Host "   启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📌 服务访问地址:" -ForegroundColor $InfoColor
Write-Host "   🌐 前端页面：http://localhost:$FRONTEND_PORT" -ForegroundColor White
Write-Host "   📖 API 文档：http://localhost:$BACKEND_PORT/swagger-ui.html" -ForegroundColor White
Write-Host ""
Write-Host "🔑 默认登录账号:" -ForegroundColor $InfoColor
Write-Host "   管理员：admin / admin123" -ForegroundColor White
Write-Host "   药房经理：manager / manager123" -ForegroundColor White
Write-Host "   库存管理员：inventory / inventory123" -ForegroundColor White
Write-Host "   销售员：sales / sales123" -ForegroundColor White
Write-Host ""
Write-Host "📝 进程信息:" -ForegroundColor $InfoColor
Write-Host "   后端作业 ID: $($backendJob.Id)" -ForegroundColor Gray
Write-Host "   前端作业 ID: $($frontendJob.Id)" -ForegroundColor Gray
Write-Host ""
Write-Host "💡 提示信息:" -ForegroundColor $InfoColor
Write-Host "   - 查看日志：Get-Job -Id $($backendJob.Id) | Receive-Job" -ForegroundColor Gray
Write-Host "   - 停止服务：Stop-Job -Id $($backendJob.Id); Stop-Job -Id $($frontendJob.Id)" -ForegroundColor Gray
Write-Host "   - 或直接关闭此窗口即可停止服务" -ForegroundColor Gray
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# 保持窗口打开
Write-Host "按任意键退出启动脚本（服务将继续在后台运行）..." -ForegroundColor $WarningColor
pause | Out-Null
