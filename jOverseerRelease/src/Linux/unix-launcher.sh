#!/bin/sh
java -Xmx512M --add-exports java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -Djava.net.preferIPv4Stack=true -Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel -Dswing.systemlaf=javax.swing.plaf.metal.MetalLookAndFeel -jar joverseer.jar $1
