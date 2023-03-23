setlocal
rem for running on Dave's machine.
set ANT=c:\lib\apache-ant-1.9.6\bin\ant.bat
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.16.101-hotspot
set JDK11_DAVE_DIR=%JAVA_HOME%
rem %ANT% test

%ANT% -f joverseerRelease\build.xml windowsInstaller
endlocal
