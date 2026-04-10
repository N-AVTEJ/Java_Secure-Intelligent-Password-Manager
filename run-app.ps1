$ErrorActionPreference = "Stop"

# Set project root and work from there
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

Write-Host "`n===============================================" -ForegroundColor Yellow
Write-Host "   PASSWORD MANAGER BUILD & RUN SYSTEM" -ForegroundColor Yellow
Write-Host "===============================================`n" -ForegroundColor Yellow

# Library and Directory Setup
$libDir = Join-Path $projectRoot "lib"
$jarPath = Join-Path $libDir "mysql-connector-j-9.3.0.jar"
$jarUrl = "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.3.0/mysql-connector-j-9.3.0.jar"

if (!(Test-Path $libDir)) {
    Write-Host "[+] Creating lib directory..." -ForegroundColor Gray
    New-Item -ItemType Directory -Path $libDir | Out-Null
}

if (!(Test-Path $jarPath)) {
    Write-Host "[!] Downloading MySQL JDBC driver (9.3.0)..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $jarUrl -OutFile $jarPath
}

if (!(Test-Path "out")) {
    Write-Host "[+] Creating output directory..." -ForegroundColor Gray
    New-Item -ItemType Directory -Path "out" | Out-Null
} else {
    Write-Host "[*] Cleaning output directory..." -ForegroundColor Gray
    Remove-Item -Path "out\*" -Recurse -Force -ErrorAction SilentlyContinue | Out-Null
}

# Dynamic Compilation
Write-Host "[*] Searching for source files..." -ForegroundColor Gray
$javaFiles = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

Write-Host "[*] Compiling project..." -ForegroundColor Cyan
javac -d out $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[!] ERROR: Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[+] Compilation successful!" -ForegroundColor Green

# Launch App
Write-Host "[*] Launching application...`n" -ForegroundColor Yellow
java -cp "out;lib/mysql-connector-j-9.3.0.jar" com.passwordmanager.Main

Write-Host "`n===============================================" -ForegroundColor Yellow
Write-Host "   APPLICATION TERMINATED" -ForegroundColor Yellow
Write-Host "===============================================`n" -ForegroundColor Yellow
