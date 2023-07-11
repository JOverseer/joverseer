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
if not %ERRORLEVEL% equ 0 echo java returned an error %ERRORLEVEL%
echo using java : %JOJAVA%
%JOJAVA% -Xmx512M  -Djava.net.preferIPv4Stack=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -Dsun.java2d.ddoffscreen=false -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true -jar joverseer.jar %1 %2 %3 %4 %5 %6 %7
if not %ERRORLEVEL% equ 0 (
  echo got error %ERRORLEVEL%
  pause
)

:end
endlocal

