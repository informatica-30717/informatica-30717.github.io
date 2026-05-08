@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0pdf.ps1" %*
exit /b %ERRORLEVEL%
