rem this is for developer testing only.
rem it allows the tester to run joverseer, using different jvms
cd /d %~dp0
rem The system default jvm
set JOJAVA=java
rem uncomment the appropriate test below to use that jvm 
rem if exist "C:\Program Files\Eclipse Adoptium\jdk-11.0.16.101-hotspot\bin" set JOJAVA="C:\Program Files\Eclipse Adoptium\jdk-11.0.16.101-hotspot\bin\java"
rem the currently installed custom jvm:
rem if exist "C:\Program Files (x86)\JOverseer\jre\bin\" set JOJAVA="C:\Program Files (x86)\JOverseer\jre\bin\java"
rem the recently built custom jvm
if exist "custom-runtime\bin\" set JOJAVA=custom-runtime\bin\java
%JOJAVA% -Xmx512M  -Djava.net.preferIPv4Stack=true -Djavax.net.debug=all --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar joverseer.jar %1
