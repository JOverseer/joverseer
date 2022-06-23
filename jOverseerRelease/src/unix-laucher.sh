#!/bin/sh
cd /joverseer
java -Xmx512M --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -Djava.net.preferIPv4Stack=true -jar joverseer.jar $1
