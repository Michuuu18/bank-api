@echo off
setlocal

echo Checking port 8081...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8081" ^| findstr LISTENING') do (
  echo Stopping process %%a using port 8081...
  taskkill /PID %%a /T /F >nul 2>&1
)

echo Starting Bank API on port 8081...
call mvnw.cmd spring-boot:run

endlocal
