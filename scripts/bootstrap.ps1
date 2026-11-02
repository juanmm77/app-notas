$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Set-Location -LiteralPath $projectRoot
$toolsRoot = Join-Path $projectRoot '.tools'
$downloads = Join-Path $toolsRoot 'downloads'
New-Item -ItemType Directory -Force -Path $downloads | Out-Null
function Get-VerifiedArchive($Uri, $Destination, $Checksum) {
    if (!(Test-Path -LiteralPath $Destination)) {
        & curl.exe -fL --retry 3 --silent --show-error $Uri -o $Destination
        if ($LASTEXITCODE -ne 0) { throw "Falló la descarga: $Uri" }
    }
    if ((Get-FileHash -LiteralPath $Destination -Algorithm SHA256).Hash.ToLowerInvariant() -ne $Checksum.Trim().ToLowerInvariant()) {
        throw "Checksum incorrecto: $Destination"
    }
}
$metadataFile = Join-Path $downloads 'jdk-metadata.json'
if (!(Test-Path -LiteralPath $metadataFile)) {
    Invoke-WebRequest 'https://api.adoptium.net/v3/assets/latest/17/hotspot?architecture=x64&image_type=jdk&os=windows&vendor=eclipse' -OutFile $metadataFile
}
$jdkMetadata = Get-Content -Raw -LiteralPath $metadataFile | ConvertFrom-Json
$jdkArchive = Join-Path $downloads 'jdk17.zip'
Get-VerifiedArchive $jdkMetadata[0].binary.package.link $jdkArchive $jdkMetadata[0].binary.package.checksum
if (!(Test-Path -LiteralPath (Join-Path $toolsRoot 'jdk'))) {
    Expand-Archive -LiteralPath $jdkArchive -DestinationPath (Join-Path $toolsRoot 'jdk')
}
$gradleHashFile = Join-Path $downloads 'gradle-8.13.sha256'
Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-8.13-bin.zip.sha256' -OutFile $gradleHashFile
$gradleHash = (Get-Content -Raw -LiteralPath $gradleHashFile).Trim()
$gradleArchive = Join-Path $downloads 'gradle-8.13-bin.zip'
Get-VerifiedArchive 'https://services.gradle.org/distributions/gradle-8.13-bin.zip' $gradleArchive $gradleHash
if (!(Test-Path -LiteralPath (Join-Path $toolsRoot 'gradle-8.13'))) {
    Expand-Archive -LiteralPath $gradleArchive -DestinationPath $toolsRoot
}
$sdkArchive = Join-Path $downloads 'android-commandline.zip'
Get-VerifiedArchive 'https://dl.google.com/android/repository/commandlinetools-win-15859902_latest.zip' $sdkArchive '90ae805d20434428bffcb699c290860f19bb5f66a67e6b330067e3de801fb04a'
$sdkRoot = Join-Path $toolsRoot 'android-sdk'
if (!(Test-Path -LiteralPath (Join-Path $sdkRoot 'cmdline-tools\current\cmdline-tools\bin\sdkmanager.bat'))) {
    Expand-Archive -LiteralPath $sdkArchive -DestinationPath (Join-Path $sdkRoot 'cmdline-tools\current')
}
$env:JAVA_HOME = (Get-ChildItem -LiteralPath (Join-Path $toolsRoot 'jdk') -Directory | Select-Object -First 1).FullName
$env:ANDROID_HOME = $sdkRoot
$env:ANDROID_USER_HOME = Join-Path $toolsRoot 'android-user'
$env:GRADLE_USER_HOME = Join-Path $toolsRoot 'gradle-user'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$sdkManager = Join-Path $sdkRoot 'cmdline-tools\current\cmdline-tools\bin\sdkmanager.bat'
1..100 | ForEach-Object { 'y' } | & $sdkManager "--sdk_root=$sdkRoot" 'platforms;android-36' 'build-tools;35.0.0' 'platform-tools'
if ($LASTEXITCODE -ne 0) { throw 'No se pudo preparar el SDK.' }
$sdkProperty = $sdkRoot.Replace('\','/').Replace(':','\:')
[System.IO.File]::WriteAllText((Join-Path $projectRoot 'local.properties'), "sdk.dir=$sdkProperty`n")
Write-Output 'Herramientas verificadas y SDK preparado.'
