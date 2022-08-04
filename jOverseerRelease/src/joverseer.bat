cd /d %~dp0
set JOJAVA=java
if exist "jre\bin\" set JOJAVA=jre\bin\java
%JOJAVA% -Xmx512M  -Djava.net.preferIPv4Stack=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar joverseer.jar %1
