cd /d %~dp0
java -Xmx512M  -Djava.net.preferIPv4Stack=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar joverseer.jar %1
