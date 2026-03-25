$mysql = "D:\MySQL\MySQL Server 8.0\bin\mysql.exe"
$rootPassword = "123456"
$projectRoot = Split-Path -Parent $PSScriptRoot
$schemaSql = Join-Path $projectRoot "sql\01_yaojie_schema.sql"
$testSql = Join-Path $projectRoot "sql\02_business_logic_test.sql"
$outputFile = Join-Path $projectRoot "scripts\last-business-test.txt"

if (-not (Test-Path $mysql)) {
  throw "MySQL client not found: $mysql"
}

Get-Content $schemaSql | & $mysql -uroot "-p$rootPassword" | Out-Null
if ($LASTEXITCODE -ne 0) {
  throw "Schema creation failed"
}

Get-Content $testSql | & $mysql -uroot "-p$rootPassword" | Tee-Object -FilePath $outputFile
if ($LASTEXITCODE -ne 0) {
  throw "Business logic test failed"
}

Write-Output ""
Write-Output "Business logic test completed."
Write-Output "Output file: $outputFile"
