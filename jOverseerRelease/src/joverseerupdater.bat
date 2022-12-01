cd /d %~dp0
set JOJAVA=java
if exist "jre\bin\" set JOJAVA=jre\bin\java
%JOJAVA%   -Djava.net.preferIPv4Stack=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar update.jar %1
