@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
if not exist joverseer.jar (
  echo Current directory is %CD%
  echo "This batch file needs to be run from directory C:\Program Files (x86)\JOverseer\"
  pause
  goto end
)
set JOJAVA=
if not exist "jre\bin\" goto notlocal
if exist "DontUseCustomJava.txt" (
  echo Ignoring custom version of java in JOverseer
  goto notlocal
)
SET count=1
set %var1%=
FOR /F "tokens=*" %%F in ('jre\bin\java -version 2^>^&1') DO (
  SET var!count!=%%F
  SET /a count=!count!+1
)
if %ERRORLEVEL% equ 0 (
  echo Found JO local version of java: %var1%
  SET JOJAVA=jre\bin\java
  goto gotJava
)
:notlocal
SET count=1
FOR /F "tokens=*" %%F in ('java -version 2^>^&1') DO (
  SET var!count!=%%F
  SET /a count=!count!+1
)
if %ERRORLEVEL% equ 0 (
  echo Found default java: %var1%
  SET JOJAVA=java
)
if %ERRORLEVEL% equ 0 goto gotJava
:noJava
echo java returned an error %ERRORLEVEL%
pause
goto end

:gotJava
echo using java : %JOJAVA%
for /F "tokens=3" %%F in ("%var1%") do set version=%%F
for /F "tokens=1,2,3 delims=." %%G in ("!version!") do (
  set MAJOR=%%G
  set MINOR=%%H
  set PATCH=%%I
)
echo !MAJOR:*"=!
set M=!MAJOR:*"=!
SET EXPORTS=
if !M! geq 9 set EXPORTS=--add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED
if !M! leq 8 (
    echo You appear to be running a really old version of java!
    echo If running the version of java embedded with JOverseer doesn't work for you, consider installing
    echo https://adoptium.net/en-GB/temurin/releases/?version=11 
)
%JOJAVA% -Xmx512M  -Djava.net.preferIPv4Stack=true %EXPORTS% -jar joverseer.jar %1 %2 %3 %4 %5 %6 %7
if not %ERRORLEVEL% equ 0 (
  echo got error %ERRORLEVEL%
  pause
)

:end
endlocal
