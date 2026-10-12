@echo off
setlocal
cd /d "%~dp0"
if not exist "dist\PagoLuz.jar" (
  call build.cmd
  if errorlevel 1 exit /b 1
)
java -jar "dist\PagoLuz.jar" %*

