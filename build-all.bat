@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Building all modules (sequential)
echo ========================================
echo.

REM Step 1: Build contracts first (they are dependencies for services)
echo [1/6] Building events-contract-restaurant...
cd events-contract-restaurant
call mvnw.cmd clean install -DskipTests
if errorlevel 1 (
    echo Failed to build events-contract-restaurant
    exit /b 1
)
cd ..
echo.

echo [2/6] Building api-contract-restaurant...
cd api-contract-restaurant
call mvnw.cmd clean install -DskipTests
if errorlevel 1 (
    echo Failed to build api-contract-restaurant
    exit /b 1
)
cd ..
echo.

REM Step 2: Build all services
echo [3/6] Building analytics-service-restaurant...
cd analytics-service-restaurant
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo Failed to build analytics-service-restaurant
    exit /b 1
)
cd ..
echo.

echo [4/6] Building audit-service-restaurant...
cd audit-service-restaurant
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo Failed to build audit-service-restaurant
    exit /b 1
)
cd ..
echo.

echo [5/6] Building notification-service-restaurant...
cd notification-service-restaurant
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo Failed to build notification-service-restaurant
    exit /b 1
)
cd ..
echo.

echo [6/6] Building rest-restaurant...
cd rest-restaurant
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo Failed to build rest-restaurant
    exit /b 1
)
cd ..
echo.

echo ========================================
echo All modules built successfully!
echo ========================================

