param([string[]]$Tasks = @('wrapper', 'testDebugUnitTest', 'lintDebug', 'assembleDebug'))
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Set-Location -LiteralPath $projectRoot
$toolsRoot = Join-Path $projectRoot '.tools'
$env:JAVA_HOME = (Get-ChildItem -LiteralPath (Join-Path $toolsRoot 'jdk') -Directory | Select-Object -First 1).FullName
$env:ANDROID_HOME = Join-Path $toolsRoot 'android-sdk'
$env:ANDROID_USER_HOME = Join-Path $toolsRoot 'android-user'
$env:GRADLE_USER_HOME = Join-Path $toolsRoot 'gradle-user'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$signingDirectory = Join-Path $toolsRoot 'signing'
$debugKey = Join-Path $signingDirectory 'debug.keystore'
if (!(Test-Path -LiteralPath $debugKey)) {
    New-Item -ItemType Directory -Force -Path $signingDirectory | Out-Null
    & (Join-Path $env:JAVA_HOME 'bin\keytool.exe') -genkeypair -keystore $debugKey -storepass android -keypass android -alias androiddebugkey -dname 'CN=Android Debug,O=Android,C=US' -keyalg RSA -keysize 2048 -validity 10000
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo generar la firma local de prueba.' }
}
& (Join-Path $toolsRoot 'gradle-8.13\bin\gradle.bat') @Tasks --console=plain --stacktrace
exit $LASTEXITCODE
