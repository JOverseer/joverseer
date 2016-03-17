cd /d %~dp0
java -Xmx512M -jar joverseer.jar -Djava.net.preferIPv4Stack=true %1
