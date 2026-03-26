param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$Username = $env:TEST_USERNAME,
    [string]$Password = $env:TEST_PASSWORD,
    [string]$BackendDir = $env:BACKEND_DIR
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-SmokeConfig -ScriptRoot $PSScriptRoot -BaseUrl $BaseUrl -BackendDir $BackendDir -Username $Username -Password $Password
$medicineUrl = "$($config.BaseUrl)/medicines?status=1&isControlled=1"
$controlledOverviewUrl = "$($config.BaseUrl)/controlled/overview"
$controlledMedicinesUrl = "$($config.BaseUrl)/controlled/medicines"
$purchasesUrl = "$($config.BaseUrl)/purchases"
$salesUrl = "$($config.BaseUrl)/sales"
$inventoryUrl = "$($config.BaseUrl)/inventories"
$usersUrl = "$($config.BaseUrl)/users"
$harness = $null

try {
    $harness = Start-BackendIfNeeded -BaseUrl $config.BaseUrl -BackendDir $config.BackendDir -LogFileName "controlled-smoke-backend.log"
    $session = New-AuthenticatedSession -LoginUrl $harness.LoginUrl -Username $config.Username -Password $config.Password
    $loginResponse = $session.LoginResponse
    $headers = $session.Headers

    $medicineResponse = Invoke-RestMethod -Uri $medicineUrl -Headers $headers -Method Get
    $controlledMedicine = @($medicineResponse.data | Select-Object -First 1)[0]
    if (-not $controlledMedicine) {
        throw "controlled medicine missing"
    }

    $batchNo = "CTRL-" + (Get-Date -Format "yyyyMMddHHmmssfff")
    $purchaseBody = @{
        supplierId = 1
        remark = "controlled smoke"
        items = @(
            @{
                medicineId = $controlledMedicine.id
                batchNo = $batchNo
                productionDate = "2026-01-01"
                expiryDate = "2027-12-31"
                quantity = 5
                purchasePrice = 18.50
            }
        )
    } | ConvertTo-Json -Depth 5

    $purchaseResponse = Invoke-RestMethod -Uri $purchasesUrl -Headers $headers -Method Post -ContentType "application/json" -Body $purchaseBody

    $overviewResponse = Invoke-RestMethod -Uri $controlledOverviewUrl -Headers $headers -Method Get
    $controlledListResponse = Invoke-RestMethod -Uri $controlledMedicinesUrl -Headers $headers -Method Get
    $batchesResponse = Invoke-RestMethod -Uri "$($config.BaseUrl)/controlled/medicines/$($controlledMedicine.id)/batches" -Headers $headers -Method Get
    $recordsResponse = Invoke-RestMethod -Uri "$($config.BaseUrl)/controlled/medicines/$($controlledMedicine.id)/records?limit=10" -Headers $headers -Method Get
    $warningsResponse = Invoke-RestMethod -Uri "$($config.BaseUrl)/controlled/medicines/$($controlledMedicine.id)/warnings?status=OPEN" -Headers $headers -Method Get

    $saleBody = @{
        customerName = "controlled smoke"
        remark = "controlled smoke"
        items = @(
            @{
                medicineId = $controlledMedicine.id
                batchNo = $batchNo
                quantity = 1
                unitPrice = 22.00
            }
        )
    } | ConvertTo-Json -Depth 5

    $saleResponse = Invoke-RestMethod -Uri $salesUrl -Headers $headers -Method Post -ContentType "application/json" -Body $saleBody
    $inventoryResponse = Invoke-RestMethod -Uri "${inventoryUrl}?medicineId=$($controlledMedicine.id)" -Headers $headers -Method Get
    $targetInventory = @($inventoryResponse.data | Where-Object { $_.batchNo -eq $batchNo } | Select-Object -First 1)[0]
    if (-not $targetInventory) {
        throw "target inventory batch missing"
    }

    $userResponse = Invoke-RestMethod -Uri $usersUrl -Headers $headers -Method Get
    $adminUser = @($userResponse.data | Where-Object { $_.username -eq $config.Username } | Select-Object -First 1)[0]
    if (-not $adminUser) {
        throw "admin user missing"
    }

    $selfDemoteBody = @{
        roleIds = @()
    } | ConvertTo-Json -Depth 5

    $selfDemoteResult = $null
    try {
        $selfDemoteResponse = Invoke-RestMethod -Uri ($usersUrl + "/" + $adminUser.id + "/roles") -Headers $headers -Method Put -ContentType "application/json" -Body $selfDemoteBody
        if ($selfDemoteResponse.code -ne 0) {
            $selfDemoteResult = $selfDemoteResponse
        }
    } catch {
        $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json
        $selfDemoteResult = $errorBody
    }

    [pscustomobject]@{
        loginUser = $loginResponse.data.user.username
        purchaseOrderNo = $purchaseResponse.data.orderNo
        saleOrderNo = $saleResponse.data.orderNo
        controlledMedicineCount = @($controlledListResponse.data).Count
        controlledOpenWarnings = $overviewResponse.data.openWarningCount
        controlledBatchCount = @($batchesResponse.data).Count
        recordCount = @($recordsResponse.data).Count
        warningCount = @($warningsResponse.data).Count
        batchAfterSaleQuantity = $targetInventory.currentQuantity
        selfDemoteCode = if ($selfDemoteResult) { $selfDemoteResult.code } else { 0 }
        selfDemoteMessage = if ($selfDemoteResult) { $selfDemoteResult.message } else { "unexpected success" }
    } | Format-List
} finally {
    Stop-BackendHarness -Harness $harness
}
