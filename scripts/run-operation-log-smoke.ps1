param(
    [string]$BaseUrl = "http://127.0.0.1:8080/api/v1",
    [string]$Username = "admin",
    [string]$Password = "123456",
    [string]$BackendDir = "C:\Users\30511\Desktop\System\backend"
)

$ErrorActionPreference = "Stop"

function Test-BackendHealth {
    param(
        [string]$HealthUrl
    )

    try {
        $response = Invoke-RestMethod -Uri $HealthUrl -Method Get -TimeoutSec 5
        return $response.code -eq 0
    } catch {
        return $false
    }
}

function Resolve-MavenCommand {
    $mavenCommand = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mavenCommand) {
        return $mavenCommand.Source
    }

    $fallback = "C:\apache-maven-4.0.0-rc-5\bin\mvn.cmd"
    if (Test-Path $fallback) {
        return $fallback
    }

    throw "mvn command not found"
}

$healthUrl = "$BaseUrl/health"
$loginUrl = "$BaseUrl/auth/login"
$logUrl = "$BaseUrl/operation-logs"
$backendJob = $null
$startedByScript = $false

try {
    if (-not (Test-BackendHealth -HealthUrl $healthUrl)) {
        $startedByScript = $true
        $mavenCommand = Resolve-MavenCommand
        $logPath = Join-Path $BackendDir "operation-log-smoke-backend.log"
        if (Test-Path $logPath) {
            Remove-Item $logPath -Force
        }

        $backendJob = Start-Job -ScriptBlock {
            param(
                [string]$WorkingDirectory,
                [string]$MavenPath,
                [string]$OutputPath
            )

            Set-Location $WorkingDirectory
            & $MavenPath "spring-boot:run" *> $OutputPath
        } -ArgumentList $BackendDir, $mavenCommand, $logPath

        $healthy = $false
        for ($i = 0; $i -lt 30; $i++) {
            Start-Sleep -Seconds 2
            if (Test-BackendHealth -HealthUrl $healthUrl) {
                $healthy = $true
                break
            }
        }

        if (-not $healthy) {
            throw "backend start timeout"
        }
    }

    $loginBody = @{
        username = $Username
        password = $Password
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method Post -ContentType "application/json" -Body $loginBody
    $accessToken = $loginResponse.data.accessToken
    if (-not $accessToken) {
        throw "accessToken missing from login response"
    }

    $headers = @{
        Authorization = "Bearer $accessToken"
    }

    $allResponse = Invoke-RestMethod -Uri $logUrl -Headers $headers -Method Get
    $saleLogUrl = $logUrl + "?moduleName=SALE&operationType=CREATE"
    $saleResponse = Invoke-RestMethod -Uri $saleLogUrl -Headers $headers -Method Get

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        totalLogs = @($allResponse.data).Count
        filteredSaleCreateLogs = @($saleResponse.data).Count
        newestModule = (@($allResponse.data) | Select-Object -First 1).moduleName
        newestOperationType = (@($allResponse.data) | Select-Object -First 1).operationType
    } | Format-List
} finally {
    if ($startedByScript -and $backendJob) {
        Stop-Job -Job $backendJob -ErrorAction SilentlyContinue | Out-Null
        Receive-Job -Job $backendJob -Keep -ErrorAction SilentlyContinue | Out-Null
        Remove-Job -Job $backendJob -Force -ErrorAction SilentlyContinue | Out-Null
    }
}
