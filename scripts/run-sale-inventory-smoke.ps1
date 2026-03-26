param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$inventoryUrl = "$($config.BaseUrl)/inventories"
$saleUrl = "$($config.BaseUrl)/sales"
$harness = $null

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "sale-inventory-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

    $inventoryResponse = Invoke-RestMethod -Uri $inventoryUrl -Headers $headers -Method Get
    $inventoryItem = @($inventoryResponse.data | Where-Object { $_.availableQuantity -gt 0 } | Select-Object -First 1)[0]
    if (-not $inventoryItem) {
        throw "no available inventory for sale smoke test"
    }

    $beforeQuantity = $inventoryItem.currentQuantity
    $saleBody = @{
        remark = "sale inventory smoke test"
        items = @(
            @{
                medicineId = $inventoryItem.medicineId
                batchNo = $inventoryItem.batchNo
                quantity = 1
            }
        )
    } | ConvertTo-Json -Depth 5

    $saleResponse = Invoke-RestMethod -Uri $saleUrl -Headers $headers -Method Post -ContentType "application/json" -Body $saleBody
    if ($saleResponse.code -ne 0) {
        throw $saleResponse.message
    }

    $afterInventoryUrl = $inventoryUrl + "?keyword=" + [uri]::EscapeDataString($inventoryItem.batchNo)
    $afterInventoryResponse = Invoke-RestMethod -Uri $afterInventoryUrl -Headers $headers -Method Get
    $afterItem = @($afterInventoryResponse.data | Where-Object {
        $_.medicineId -eq $inventoryItem.medicineId -and $_.batchNo -eq $inventoryItem.batchNo
    } | Select-Object -First 1)[0]

    if (-not $afterItem) {
        throw "inventory row missing after sale"
    }

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        medicineName = $inventoryItem.medicineName
        batchNo = $inventoryItem.batchNo
        orderNo = $saleResponse.data.orderNo
        beforeQuantity = $beforeQuantity
        afterQuantity = $afterItem.currentQuantity
        availableQuantity = $afterItem.availableQuantity
    } | Format-List
} finally {
    Stop-BackendHarness -Harness $harness
}
