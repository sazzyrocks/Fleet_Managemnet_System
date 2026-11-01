# ==============================================================================
# Fleet Management System - Build, Test & Package Automation Script
# ==============================================================================
param (
    [switch]$SkipTests = $false,
    [switch]$Package = $true
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "Fleet Management System — Automated Build & Test Runner" -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan

# 1. Detect Maven
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnCmd) {
    $mvnCmd = Get-Command mvn.cmd -ErrorAction SilentlyContinue
}

if ($mvnCmd) {
    Write-Host "[INFO] Maven detected: $($mvnCmd.Source)" -ForegroundColor Green
    
    $goal = "clean test"
    if ($Package) {
        $goal = "clean package"
    }
    if ($SkipTests) {
        $goal += " -DskipTests"
    }

    Write-Host "[EXEC] Running: mvn $goal" -ForegroundColor Yellow
    & $mvnCmd.Source $goal.Split(" ")
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n[SUCCESS] Maven build, tests, and packaging completed successfully!" -ForegroundColor Green
        if ($Package) {
            $jar = Get-ChildItem -Path "target" -Filter "*executable.jar" -ErrorAction SilentlyContinue
            if ($jar) {
                Write-Host "[PACKAGE] Executable Fat JAR generated at: $($jar.FullName)" -ForegroundColor Cyan
            }
        }
    } else {
        Write-Host "`n[ERROR] Maven build failed with exit code $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
} else {
    Write-Host "[WARN] 'mvn' command not found on system PATH." -ForegroundColor Yellow
    Write-Host "[INFO] Verifying Java compiler availability..." -ForegroundColor Cyan
    
    $javacCmd = Get-Command javac -ErrorAction SilentlyContinue
    if (-not $javacCmd -and (Test-Path "C:\Program Files\Java\jdk-26.0.2\bin\javac.exe")) {
        $javacCmd = "C:\Program Files\Java\jdk-26.0.2\bin\javac.exe"
    }

    if ($javacCmd) {
        Write-Host "[INFO] JDK compiler located: $javacCmd" -ForegroundColor Green
        Write-Host "[INFO] To run the full Maven build and create the executable JAR, install Maven or add it to PATH:" -ForegroundColor Yellow
        Write-Host "       winget install Apache.Maven" -ForegroundColor White
        Write-Host "       Then run: mvn clean package`n" -ForegroundColor White
    } else {
        Write-Host "[ERROR] Neither Maven nor JDK compiler was found on PATH." -ForegroundColor Red
    }
}

