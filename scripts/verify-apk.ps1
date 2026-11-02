param([string]$Apk = 'app/build/outputs/apk/debug/app-debug.apk')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Set-Location -LiteralPath $projectRoot
$env:JAVA_HOME = (Get-ChildItem -LiteralPath (Join-Path $projectRoot '.tools\jdk') -Directory | Select-Object -First 1).FullName
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$apkPath = (Resolve-Path -LiteralPath $Apk).Path
$buildTools = Join-Path $projectRoot '.tools\android-sdk\build-tools\35.0.0'
& (Join-Path $buildTools 'apksigner.bat') verify --verbose --print-certs $apkPath
if ($LASTEXITCODE -ne 0) { throw 'La firma del APK no pasó la verificación.' }
& (Join-Path $buildTools 'aapt2.exe') dump badging $apkPath
if ($LASTEXITCODE -ne 0) { throw 'No se pudo inspeccionar el paquete.' }
Get-FileHash -LiteralPath $apkPath -Algorithm SHA256
