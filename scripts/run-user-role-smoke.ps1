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
$usersUrl = "$BaseUrl/users"
$rolesUrl = "$BaseUrl/roles"
$backendJob = $null
$startedByScript = $false

try {
    if (-not (Test-BackendHealth -HealthUrl $healthUrl)) {
        $startedByScript = $true
        $mavenCommand = Resolve-MavenCommand
        $logPath = Join-Path $BackendDir "user-role-smoke-backend.log"
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

    $userResponse = Invoke-RestMethod -Uri $usersUrl -Headers $headers -Method Get
    $roleResponse = Invoke-RestMethod -Uri $rolesUrl -Headers $headers -Method Get
    $targetUser = @($userResponse.data | Where-Object { $_.username -ne 'admin' } | Select-Object -First 1)[0]
    if (-not $targetUser) {
        throw "target user missing"
    }

    $assignUrl = $usersUrl + "/" + $targetUser.id + "/roles"
    $assignBody = @{
        roleIds = @($targetUser.roleIds)
    } | ConvertTo-Json -Depth 5
    $assignResponse = Invoke-RestMethod -Uri $assignUrl -Headers $headers -Method Put -ContentType "application/json" -Body $assignBody

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        totalUsers = @($userResponse.data).Count
        totalRoles = @($roleResponse.data).Count
        targetUsername = $targetUser.username
        assignedRoleCount = @($assignResponse.data.roleIds).Count
        assignedRoleCodes = ($assignResponse.data.roleCodes -join ",")
    } | Format-List
} finally {
    if ($startedByScript -and $backendJob) {
        Stop-Job -Job $backendJob -ErrorAction SilentlyContinue | Out-Null
        Receive-Job -Job $backendJob -Keep -ErrorAction SilentlyContinue | Out-Null
        Remove-Job -Job $backendJob -Force -ErrorAction SilentlyContinue | Out-Null
    }
}
