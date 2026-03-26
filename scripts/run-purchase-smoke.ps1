param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

$config = $null
$harness = $null

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password

$supplierUrl = "$($config.BaseUrl)/medicines/suppliers"
$medicineUrl = "$($config.BaseUrl)/medicines?status=1"
$purchaseUrl = "$($config.BaseUrl)/purchases"

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "purchase-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

    $supplierResponse = Invoke-RestMethod -Uri $supplierUrl -Headers $headers -Method Get
    $medicineResponse = Invoke-RestMethod -Uri $medicineUrl -Headers $headers -Method Get

    $supplier = @($supplierResponse.data | Where-Object { $_.status -eq 1 } | Select-Object -First 1)[0]
    if (-not $supplier) {
        $supplier = @($supplierResponse.data | Select-Object -First 1)[0]
    }

    $medicine = @($medicineResponse.data | Select-Object -First 1)[0]

    if (-not $supplier) {
        throw "no supplier available for purchase smoke test"
    }
    if (-not $medicine) {
        throw "no medicine available for purchase smoke test"
    }

    $batchNo = "SMOKE-" + (Get-Date -Format "yyyyMMddHHmmss")
    $purchaseBody = @{
        supplierId = $supplier.id
        remark = "purchase smoke test"
        items = @(
            @{
                medicineId = $medicine.id
                batchNo = $batchNo
                quantity = 5
                purchasePrice = 12.50
                productionDate = "2026-03-01"
                expiryDate = "2027-03-01"
            }
        )
    } | ConvertTo-Json -Depth 6

    $createResponse = Invoke-RestMethod -Uri $purchaseUrl -Headers $headers -Method Post -ContentType "application/json" -Body $purchaseBody
    $listResponse = Invoke-RestMethod -Uri $purchaseUrl -Headers $headers -Method Get

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        supplierName = $supplier.supplierName
        medicineName = $medicine.medicineName
        createdOrderNo = $createResponse.data.orderNo
        createdTotalAmount = $createResponse.data.totalAmount
        purchaseCount = @($listResponse.data).Count
        newestOrderNo = (@($listResponse.data) | Select-Object -First 1).orderNo
    } | Format-List
} finally {
    Stop-BackendHarness -Harness $harness
}
