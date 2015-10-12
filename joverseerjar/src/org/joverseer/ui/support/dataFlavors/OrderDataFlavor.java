package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Order;

/**
 * Data flavor for Order
 * @author Marios Skounakis
 */
public class OrderDataFlavor extends DataFlavor {

    private static final long serialVersionUID = -9020952126767280123L;

    public OrderDataFlavor() throws ClassNotFoundException {
        super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Order.class.getName());
    }
}
