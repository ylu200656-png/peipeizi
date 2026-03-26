Set-StrictMode -Version Latest

function Resolve-OptionalValue {
    param(
        [AllowEmptyString()]
        [string]$Preferred,
        [AllowEmptyString()]
        [string]$EnvironmentValue,
        [AllowEmptyString()]
        [string]$DefaultValue
    )

    if (-not [string]::IsNullOrWhiteSpace($Preferred)) {
        return $Preferred
    }

    if (-not [string]::IsNullOrWhiteSpace($EnvironmentValue)) {
        return $EnvironmentValue
    }

    return $DefaultValue
}

function Get-ProjectRoot {
    param(
        [string]$ScriptRoot
    )

    return Split-Path -Parent $ScriptRoot
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

function Resolve-MySqlCommand {
    param(
        [AllowEmptyString()]
        [string]$Preferred
    )

    $resolvedPath = Resolve-OptionalValue -Preferred $Preferred -EnvironmentValue $env:MYSQL_EXE -DefaultValue ""
    if (-not [string]::IsNullOrWhiteSpace($resolvedPath)) {
        if (-not (Test-Path $resolvedPath)) {
            throw "MySQL client not found: $resolvedPath"
        }
        return $resolvedPath
    }

    $mysqlCommand = Get-Command mysql -ErrorAction SilentlyContinue
    if ($mysqlCommand) {
        return $mysqlCommand.Source
    }

    $legacyFallback = "D:\MySQL\MySQL Server 8.0\bin\mysql.exe"
    if (Test-Path $legacyFallback) {
        return $legacyFallback
    }

    throw "mysql command not found. Set MYSQL_EXE or pass -MySqlExe."
}

function Get-SmokeConfig {
    param(
        [string]$ScriptRoot,
        [AllowEmptyString()]
        [string]$BaseUrl,
        [AllowEmptyString()]
        [string]$BackendDir,
        [AllowEmptyString()]
        [string]$Username,
        [AllowEmptyString()]
        [string]$Password
    )

    $projectRoot = Get-ProjectRoot -ScriptRoot $ScriptRoot
    return [pscustomobject]@{
        ProjectRoot = $projectRoot
        BaseUrl = Resolve-OptionalValue -Preferred $BaseUrl -EnvironmentValue $env:BASE_URL -DefaultValue "http://127.0.0.1:8080/api/v1"
        BackendDir = Resolve-OptionalValue -Preferred $BackendDir -EnvironmentValue $env:BACKEND_DIR -DefaultValue (Join-Path $projectRoot "backend")
        Username = Resolve-OptionalValue -Preferred $Username -EnvironmentValue $env:TEST_USERNAME -DefaultValue "admin"
        Password = Resolve-OptionalValue -Preferred $Password -EnvironmentValue $env:TEST_PASSWORD -DefaultValue "123456"
    }
}

function Get-BusinessTestConfig {
    param(
        [string]$ScriptRoot,
        [AllowEmptyString()]
        [string]$MySqlExe,
        [AllowEmptyString()]
        [string]$MySqlPassword
    )

    $projectRoot = Get-ProjectRoot -ScriptRoot $ScriptRoot
    return [pscustomobject]@{
        ProjectRoot = $projectRoot
        MySqlExe = Resolve-MySqlCommand -Preferred $MySqlExe
        MySqlPassword = Resolve-OptionalValue -Preferred $MySqlPassword -EnvironmentValue $env:MYSQL_PASSWORD -DefaultValue "123456"
        SchemaSql = Join-Path $projectRoot "sql\01_yaojie_schema.sql"
        TestSql = Join-Path $projectRoot "sql\02_business_logic_test.sql"
        OutputFile = Join-Path $projectRoot "scripts\last-business-test.txt"
    }
}

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

function Start-BackendIfNeeded {
    param(
        [string]$BaseUrl,
        [string]$BackendDir,
        [string]$LogFileName
    )

    $healthUrl = "$BaseUrl/health"
    $harness = [pscustomobject]@{
        BaseUrl = $BaseUrl
        HealthUrl = $healthUrl
        LoginUrl = "$BaseUrl/auth/login"
        BackendDir = $BackendDir
        StartedByScript = $false
        BackendJob = $null
        LogPath = Join-Path $BackendDir $LogFileName
    }

    if (Test-BackendHealth -HealthUrl $healthUrl) {
        return $harness
    }

    $mavenCommand = Resolve-MavenCommand
    if (Test-Path $harness.LogPath) {
        Remove-Item $harness.LogPath -Force
    }

    $harness.StartedByScript = $true
    $harness.BackendJob = Start-Job -ScriptBlock {
        param(
            [string]$WorkingDirectory,
            [string]$MavenPath,
            [string]$OutputPath
        )

        Set-Location $WorkingDirectory
        & $MavenPath "spring-boot:run" *> $OutputPath
    } -ArgumentList $BackendDir, $mavenCommand, $harness.LogPath

    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Seconds 2
        if (Test-BackendHealth -HealthUrl $healthUrl) {
            return $harness
        }
    }

    throw "backend start timeout"
}

function Stop-BackendHarness {
    param(
        [pscustomobject]$Harness
    )

    if (-not $Harness) {
        return
    }

    if ($Harness.StartedByScript -and $Harness.BackendJob) {
        Stop-Job -Job $Harness.BackendJob -ErrorAction SilentlyContinue | Out-Null
        Receive-Job -Job $Harness.BackendJob -Keep -ErrorAction SilentlyContinue | Out-Null
        Remove-Job -Job $Harness.BackendJob -Force -ErrorAction SilentlyContinue | Out-Null
    }
}

function New-AuthenticatedSession {
    param(
        [string]$LoginUrl,
        [string]$Username,
        [string]$Password
    )

    $loginBody = @{
        username = $Username
        password = $Password
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri $LoginUrl -Method Post -ContentType "application/json" -Body $loginBody
    $accessToken = $loginResponse.data.accessToken
    if (-not $accessToken) {
        throw "accessToken missing from login response"
    }

    return [pscustomobject]@{
        LoginResponse = $loginResponse
        Headers = @{
            Authorization = "Bearer $accessToken"
        }
    }
}
