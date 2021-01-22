cd /d %~dp0
java -Xmx512M -jar joverseer.jar -Djava.net.preferIPv4Stack=true -Dsun.java2d.ddoffscreen=false -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true %1
