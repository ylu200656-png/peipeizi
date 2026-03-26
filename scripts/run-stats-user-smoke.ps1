param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

function Invoke-Login {
    param(
        [string]$Url,
        [string]$LoginUsername,
        [string]$LoginPassword
    )

    $loginBody = @{
        username = $LoginUsername
        password = $LoginPassword
    } | ConvertTo-Json

    return Invoke-RestMethod -Uri $Url -Method Post -ContentType "application/json" -Body $loginBody
}

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$loginUrl = "$($config.BaseUrl)/auth/login"
$usersUrl = "$($config.BaseUrl)/users"
$rolesUrl = "$($config.BaseUrl)/roles"
$statsOverviewUrl = "$($config.BaseUrl)/stats/overview"
$statsTrendUrl = "$($config.BaseUrl)/stats/trend?days=7"
$statsInventoryUrl = "$($config.BaseUrl)/stats/inventory-category"
$statsWarningUrl = "$($config.BaseUrl)/stats/warning-distribution"
$harness = $null

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "stats-user-smoke-backend.log"
    $loginResponse = Invoke-Login -Url $loginUrl -LoginUsername $config.Username -LoginPassword $config.Password
    $accessToken = $loginResponse.data.accessToken
    if (-not $accessToken) {
        throw "accessToken missing from login response"
    }

    $headers = @{
        Authorization = "Bearer $accessToken"
    }

    $overviewResponse = Invoke-RestMethod -Uri $statsOverviewUrl -Headers $headers -Method Get
    $trendResponse = Invoke-RestMethod -Uri $statsTrendUrl -Headers $headers -Method Get
    $inventoryResponse = Invoke-RestMethod -Uri $statsInventoryUrl -Headers $headers -Method Get
    $warningResponse = Invoke-RestMethod -Uri $statsWarningUrl -Headers $headers -Method Get
    $roleResponse = Invoke-RestMethod -Uri $rolesUrl -Headers $headers -Method Get

    $salesRole = @($roleResponse.data | Where-Object { $_.roleCode -eq 'SALES_CLERK' } | Select-Object -First 1)[0]
    if (-not $salesRole) {
        throw "SALES_CLERK role missing"
    }

    $timestamp = Get-Date -Format "yyyyMMddHHmmssfff"
    $createdUsername = "smoke_user_$timestamp"
    $initialPassword = "Pass@123"
    $resetPassword = "Pass@456"
    $createUserBody = @{
        username = $createdUsername
        realName = "冒烟用户"
        password = $initialPassword
        status = 1
        roleIds = @($salesRole.id)
    } | ConvertTo-Json -Depth 5

    $createUserResponse = Invoke-RestMethod -Uri $usersUrl -Headers $headers -Method Post -ContentType "application/json" -Body $createUserBody
    $createdUser = $createUserResponse.data

    $initialLoginResponse = Invoke-Login -Url $loginUrl -LoginUsername $createdUsername -LoginPassword $initialPassword
    $disableResponse = Invoke-RestMethod -Uri ($usersUrl + "/" + $createdUser.id + "/status") -Headers $headers -Method Put -ContentType "application/json" -Body (@{ status = 0 } | ConvertTo-Json)
    $disabledLoginResponse = Invoke-Login -Url $loginUrl -LoginUsername $createdUsername -LoginPassword $initialPassword
    $enableResponse = Invoke-RestMethod -Uri ($usersUrl + "/" + $createdUser.id + "/status") -Headers $headers -Method Put -ContentType "application/json" -Body (@{ status = 1 } | ConvertTo-Json)
    Invoke-RestMethod -Uri ($usersUrl + "/" + $createdUser.id + "/reset-password") -Headers $headers -Method Put -ContentType "application/json" -Body (@{ newPassword = $resetPassword } | ConvertTo-Json) | Out-Null
    $resetLoginResponse = Invoke-Login -Url $loginUrl -LoginUsername $createdUsername -LoginPassword $resetPassword

    [pscustomobject]@{
        adminUser = $loginResponse.data.user.username
        trendPointCount = @($trendResponse.data).Count
        inventoryCategoryCount = @($inventoryResponse.data).Count
        warningDistributionCount = @($warningResponse.data).Count
        openWarningCount = $overviewResponse.data.openWarningCount
        createdUsername = $createdUser.username
        createdUserStatus = $createdUser.status
        createdUserRoleCodes = ($createdUser.roleCodes -join ",")
        initialLoginCode = $initialLoginResponse.code
        disabledStatus = $disableResponse.data.status
        disabledLoginCode = $disabledLoginResponse.code
        enabledStatus = $enableResponse.data.status
        resetLoginCode = $resetLoginResponse.code
    } | Format-List
} finally {
    Stop-BackendHarness -Harness $harness
}
