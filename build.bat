setlocal
set ANT=c:\lib\apache-ant-1.9.6\bin\ant.bat
set JAVA_HOME=c:\Program Files\Java\jdk1.8.0_172
%ANT% test
rem %ANT% -f joverseerRelease\build.xml
endlocal
