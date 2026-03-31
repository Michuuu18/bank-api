@echo off
REM Ten sam start co start-local.cmd - uzyj tego zamiast samego mvnw spring-boot:run, gdy port 8081 jest zajety.
call "%~dp0start-local.cmd" %*
