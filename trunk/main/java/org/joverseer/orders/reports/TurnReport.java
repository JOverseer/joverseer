package org.joverseer.orders.reports;

import org.joverseer.support.Container;


public class TurnReport {
    Container reportItems = new Container(new String[]{"nationNo"});
    
    public Container getReportItems() {
        return reportItems;
    }
    
    public void setReportItems(Container reportItems) {
        this.reportItems = reportItems;
    }
    
}
