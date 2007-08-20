package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Order;

/**
 * Data flavor for Order
 * @author Marios Skounakis
 */
public class OrderDataFlavor extends DataFlavor {
    public OrderDataFlavor() throws ClassNotFoundException {
       super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Order.class.getName());
    }
}
