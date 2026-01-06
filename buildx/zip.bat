@echo off
REM Zip packaging script that preserves file attributes as much as possible

setlocal enabledelayedexpansion

REM Check parameters
if "%~2"=="" (
    echo Usage: %~n0 source_dir output_zip
    exit /b 1
)

set SOURCE_DIR=%~1
set OUTPUT_ZIP=%~2

REM Use PowerShell's Compress-Archive command with -Force to overwrite existing files and -CompressionLevel set to Optimal
REM Use * to include only the contents of the source directory, not the directory itself
PowerShell -Command "Compress-Archive -Path '%SOURCE_DIR%\*' -DestinationPath '%OUTPUT_ZIP%' -Force -CompressionLevel Optimal"

if %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to create zip file
    exit /b 1
)

echo Successfully created zip file: %OUTPUT_ZIP%
exit /b 0