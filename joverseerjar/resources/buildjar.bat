setlocal
set PATH=c:\glassfish4\jdk7\bin;%PATH%
set ant=c:\lib\apache-ant-1.7.1\bin\ant

%ant% -f joverseepackageant.xml create_joverseer_jar create_jnlp create_updaterjar create_updatezip

pause
endload
