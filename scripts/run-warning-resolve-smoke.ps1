param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR,
    [string]$MySqlExe = $env:MYSQL_EXE,
    [string]$MySqlPassword = $env:MYSQL_PASSWORD
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$dbConfig = Get-BusinessTestConfig -ScriptRoot $PSScriptRoot -MySqlExe $MySqlExe -MySqlPassword $MySqlPassword
$patchSql = Join-Path $dbConfig.ProjectRoot "sql\03_warning_resolve_patch.sql"
$medicinesUrl = "$($config.BaseUrl)/medicines?status=1"
$warningsUrl = "$($config.BaseUrl)/warnings"
$warningSummaryUrl = "$($config.BaseUrl)/warnings/summary"
$logUrl = "$($config.BaseUrl)/operation-logs?moduleName=WARNING&operationType=RESOLVE"
$harness = $null

function Invoke-MySqlScript {
    param(
        [string]$ScriptPath
    )

    Get-Content $ScriptPath | & $dbConfig.MySqlExe -uroot "-p$($dbConfig.MySqlPassword)" | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to execute MySQL script: $ScriptPath"
    }
}

try {
    Invoke-MySqlScript -ScriptPath $patchSql

    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "warning-resolve-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

    $medicineResponse = Invoke-RestMethod -Uri $medicinesUrl -Headers $headers -Method Get
    $medicine = @($medicineResponse.data | Select-Object -First 1)[0]
    if (-not $medicine) {
        throw "medicine missing"
    }

    $seedMessage = "Smoke warning for resolve"
    $seedRemark = "warning resolve smoke"
    $seedSql = @"
USE yaojie;
DELETE FROM warning_record WHERE warning_message = '$seedMessage';
INSERT INTO warning_record (medicine_id, batch_no, warning_type, warning_level, warning_message, status, created_at, handled_at, handled_by, handle_remark)
VALUES ($($medicine.id), 'SMOKE-WARN-001', 'LOW_STOCK', 'WARN', '$seedMessage', 'OPEN', NOW(), NULL, NULL, NULL);
SELECT LAST_INSERT_ID();
"@

    $warningId = ($seedSql | & $dbConfig.MySqlExe -uroot "-p$($dbConfig.MySqlPassword)" --batch --skip-column-names) | Select-Object -Last 1
    if (-not $warningId) {
        throw "warning seed failed"
    }

    $beforeSummary = Invoke-RestMethod -Uri $warningSummaryUrl -Headers $headers -Method Get
    $resolveBody = @{
        handleRemark = $seedRemark
    } | ConvertTo-Json

    $resolveResponse = Invoke-RestMethod -Uri "${warningsUrl}/$warningId/resolve" -Headers $headers -Method Put -ContentType "application/json" -Body $resolveBody
    $resolvedList = Invoke-RestMethod -Uri "${warningsUrl}?status=RESOLVED" -Headers $headers -Method Get
    $resolvedWarning = @($resolvedList.data | Where-Object { $_.id -eq [int64]$warningId } | Select-Object -First 1)[0]
    if (-not $resolvedWarning) {
        throw "resolved warning missing"
    }

    $afterSummary = Invoke-RestMethod -Uri $warningSummaryUrl -Headers $headers -Method Get
    $logResponse = Invoke-RestMethod -Uri $logUrl -Headers $headers -Method Get

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        warningId = [int64]$warningId
        beforeOpenWarnings = $beforeSummary.data.openTotal
        afterOpenWarnings = $afterSummary.data.openTotal
        resolvedStatus = $resolveResponse.data.status
        handlerName = $resolvedWarning.handlerName
        handleRemark = $resolvedWarning.handleRemark
        handledAt = $resolvedWarning.handledAt
        resolveLogCount = @($logResponse.data | Where-Object { $_.businessNo -eq "$warningId" }).Count
    } | Format-List
} finally {
    Stop-BackendHarness -Harness $harness
}
