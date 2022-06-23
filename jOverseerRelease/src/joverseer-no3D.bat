cd /d %~dp0
java -Xmx512M -Djava.net.preferIPv4Stack=true -Dsun.java2d.ddoffscreen=false -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar joverseer.jar %1
