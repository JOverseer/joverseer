@echo off
rem A batch file to invoke java with the right options to start JOverseer,
rem when the default settings in the .exe are inappropriate.
rem
rem Preference is given to any java VM embedded with JOverseer.
rem
rem To force the use of your own version of java over the embedded version,
rem just create the file DontUseCustomJava.txt in the JO directory,  
rem or add DontUseCustomJava as the first argument.
rem
rem To force disable the 2d/3d options causing problems for some NVidia drivers,
rem just create the file DontUse3d.txt in the JO directory,
rem or add DontUse3d as the first or second argument.
rem
SETLOCAL ENABLEDELAYEDEXPANSION
if not exist joverseer.jar (
  echo Current directory is "%CD%"
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
if "%1"=="DontUseCustomJava" (
  echo Ignoring custom version of java in JOverseer
  shift
  goto notlocal
)
SET count=1
set var1=
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
set NO3D=
if exist "DontUse3d.txt" (
  echo Disabling 2d/3d options
  set NO3D=-Dsun.java2d.ddoffscreen=false -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true
)
if "%1"=="DontUse3d" (
  echo Disabling 2d/3d options
  set NO3D=-Dsun.java2d.ddoffscreen=false -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true
  shift
)
echo %JOJAVA% -Xmx512M  -Djava.net.preferIPv4Stack=true %EXPORTS% %NO3D% -jar joverseer.jar %1 %2 %3 %4 %5 %6 %7
%JOJAVA% -Xmx512M  -Djava.net.preferIPv4Stack=true %EXPORTS% %NO3D% -jar joverseer.jar %1 %2 %3 %4 %5 %6 %7
if not %ERRORLEVEL% equ 0 (
  echo got error %ERRORLEVEL%
  pause
)

:end
endlocal
