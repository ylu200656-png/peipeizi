param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$warningSummaryUrl = "$($config.BaseUrl)/warnings/summary"
$warningListUrl = "$($config.BaseUrl)/warnings?status=OPEN"
$statsUrl = "$($config.BaseUrl)/stats/overview"
$harness = $null

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "warning-dashboard-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

    $warningSummaryResponse = Invoke-RestMethod -Uri $warningSummaryUrl -Headers $headers -Method Get
    $warningListResponse = Invoke-RestMethod -Uri $warningListUrl -Headers $headers -Method Get
    $statsResponse = Invoke-RestMethod -Uri $statsUrl -Headers $headers -Method Get

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        openWarningTotal = $warningSummaryResponse.data.openTotal
        warningListCount = @($warningListResponse.data).Count
        lowStockCount = $warningSummaryResponse.data.lowStockCount
        expirySoonCount = $warningSummaryResponse.data.expirySoonCount
        expiredCount = $warningSummaryResponse.data.expiredCount
        medicineCount = $statsResponse.data.medicineCount
        inventoryBatchCount = $statsResponse.data.inventoryBatchCount
        availableStockTotal = $statsResponse.data.availableStockTotal
        openWarningCountFromStats = $statsResponse.data.openWarningCount
        latestWarningCount = @($statsResponse.data.latestWarnings).Count
        todayPurchaseAmount = $statsResponse.data.todayPurchaseAmount
        todaySaleAmount = $statsResponse.data.todaySaleAmount
    } | Format-List
} finally {
    Stop-BackendHarness -Harness $harness
}
