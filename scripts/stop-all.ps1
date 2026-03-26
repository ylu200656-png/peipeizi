# 药捷系统一键停止脚本
# 功能：停止前后端服务，释放端口

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   药捷药品管理系统 - 一键停止脚本" -ForegroundColor Cyan
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
        [int]$Port,
        [string]$ServiceName
    )
    
    Write-Host "停止 $ServiceName (端口 $Port)..." -ForegroundColor $InfoColor
    
    $processId = Get-ProcessByPort -Port $Port
    
    if ($processId) {
        try {
            $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
            if ($process) {
                Write-Host "  找到进程：$($process.Name) (PID: $processId)" -ForegroundColor $WarningColor
                Write-Host "  正在终止进程..." -ForegroundColor $WarningColor
                Stop-Process -Id $processId -Force
                Start-Sleep -Seconds 1
                
                # 验证是否成功停止
                $stillRunning = Get-Process -Id $processId -ErrorAction SilentlyContinue
                if (-not $stillRunning) {
                    Write-Host "  ✓ $ServiceName 已停止" -ForegroundColor $SuccessColor
                    return $true
                } else {
                    Write-Host "  ✗ $ServiceName 停止失败" -ForegroundColor $ErrorColor
                    return $false
                }
            }
        } catch {
            Write-Host "  ✗ 停止进程失败：$_" -ForegroundColor $ErrorColor
            return $false
        }
    } else {
        Write-Host "  ℹ 未检测到运行中的进程" -ForegroundColor $Gray
        return $true
    }
}

# 函数：停止后台作业
function Stop-BackgroundJobs {
    Write-Host "检查后台作业..." -ForegroundColor $InfoColor
    
    $jobs = Get-Job | Where-Object {
        $_.Command -like '*mvn spring-boot:run*' -or 
        $_.Command -like '*npm run dev*'
    }
    
    if ($jobs) {
        foreach ($job in $jobs) {
            Write-Host "  停止作业：$($job.Id) ($($job.Command))" -ForegroundColor $WarningColor
            Stop-Job -Job $job
            Remove-Job -Job $job
        }
        Write-Host "  ✓ 后台作业已清理" -ForegroundColor $SuccessColor
    } else {
        Write-Host "  ℹ 未发现相关后台作业" -ForegroundColor $Gray
    }
}

# 主流程
Write-Host ""

# 1. 停止后台作业
Stop-BackgroundJobs
Write-Host ""

# 2. 停止后端
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   停止服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$backendStopped = Stop-ProcessByPort -Port $BACKEND_PORT -ServiceName "后端"
Write-Host ""

# 3. 停止前端
$frontendStopped = Stop-ProcessByPort -Port $FRONTEND_PORT -ServiceName "前端"
Write-Host ""

# 4. 显示结果
if ($backendStopped -and $frontendStopped) {
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "   所有服务已成功停止！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "✓ 后端端口 $BACKEND_PORT 已释放" -ForegroundColor $SuccessColor
    Write-Host "✓ 前端端口 $FRONTEND_PORT 已释放" -ForegroundColor $SuccessColor
    Write-Host ""
} else {
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "   部分服务停止失败" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host ""
    
    if (-not $backendStopped) {
        Write-Host "✗ 后端未能完全停止，请手动检查" -ForegroundColor $ErrorColor
    }
    if (-not $frontendStopped) {
        Write-Host "✗ 前端未能完全停止，请手动检查" -ForegroundColor $ErrorColor
    }
    
    Write-Host ""
    Write-Host "可以使用以下命令手动查看占用进程：" -ForegroundColor $WarningColor
    Write-Host "  netstat -ano | findstr :$BACKEND_PORT" -ForegroundColor $Gray
    Write-Host "  netstat -ano | findstr :$FRONTEND_PORT" -ForegroundColor $Gray
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor $InfoColor
pause | Out-Null
