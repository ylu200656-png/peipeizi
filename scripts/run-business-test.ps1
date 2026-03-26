param(
    [string]$MySqlExe = $env:MYSQL_EXE,
    [string]$MySqlPassword = $env:MYSQL_PASSWORD
)

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "lib\TestHarness.ps1")

$config = Get-BusinessTestConfig -ScriptRoot $PSScriptRoot -MySqlExe $MySqlExe -MySqlPassword $MySqlPassword

Get-Content $config.SchemaSql | & $config.MySqlExe -uroot "-p$($config.MySqlPassword)" | Out-Null
if ($LASTEXITCODE -ne 0) {
  throw "Schema creation failed"
}

Get-Content $config.TestSql | & $config.MySqlExe -uroot "-p$($config.MySqlPassword)" | Tee-Object -FilePath $config.OutputFile
if ($LASTEXITCODE -ne 0) {
  throw "Business logic test failed"
}

Write-Output ""
Write-Output "Business logic test completed."
Write-Output "Output file: $($config.OutputFile)"
