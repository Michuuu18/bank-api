@echo off
setlocal
echo Zwalnianie portu 8081 (jesli zajety przez stara instancje aplikacji)...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$pids = @(Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue ^| Where-Object { $_.State -eq 'Listen' } ^| Select-Object -ExpandProperty OwningProcess -Unique ^| Where-Object { $_ -gt 0 }); ^
   foreach ($id in $pids) { Write-Host ('  Zatrzymuje PID ' + $id); Stop-Process -Id $id -Force -ErrorAction SilentlyContinue }"
if errorlevel 1 (
  echo Uwaga: nie udalo sie uzyc PowerShell do zwolnienia portu. Sprobuj recznie: netstat -ano ^| findstr :8081
)
echo.
echo Uruchamianie Bank API (domyslnie port 8081, lub zmienna SERVER_PORT)...
call mvnw.cmd spring-boot:run %*
endlocal
