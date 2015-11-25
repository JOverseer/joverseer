package org.joverseer.orders.reports;


public class ReportItem {
    int nationNo;
    Object source;
    Object item;
    ReportItemDetailEnum detail;
    
    public ReportItemDetailEnum getDetail() {
        return this.detail;
    }
    
    public void setDetail(ReportItemDetailEnum detail) {
        this.detail = detail;
    }
    
    public Object getItem() {
        return this.item;
    }
    
    public void setItem(Object item) {
        this.item = item;
    }
    
    public int getNationNo() {
        return this.nationNo;
    }
    
    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public Object getSource() {
        return this.source;
    }
    
    public void setSource(Object source) {
        this.source = source;
    }

    public ReportItem(int nationNo, Object source, Object item, ReportItemDetailEnum detail) {
        super();
        this.source = source;
        this.nationNo = nationNo;
        this.item = item;
        this.detail = detail;
    }

    
}
