cd /d %~dp0
set JOJAVA=java
if exist "jre\bin\" set JOJAVA=jre\bin\java
%JOJAVA% -Xmx512M -Djava.net.preferIPv4Stack=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -Dsun.java2d.ddoffscreen=false -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar joverseer.jar %1
