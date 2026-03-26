param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$logUrl = "$($config.BaseUrl)/operation-logs"
$harness = $null

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "operation-log-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

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
    Stop-BackendHarness -Harness $harness
}
