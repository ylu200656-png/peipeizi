param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$usersUrl = "$($config.BaseUrl)/users"
$rolesUrl = "$($config.BaseUrl)/roles"
$harness = $null

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "user-role-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

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
    Stop-BackendHarness -Harness $harness
}
