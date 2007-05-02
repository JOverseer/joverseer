package org.joverseer.orders.checks;

import org.joverseer.domain.Order;


public abstract class AbstractCheck {
    int paramNo;

    public AbstractCheck() {
    }
    
    public AbstractCheck(int paramNo) {
        this.paramNo = paramNo;
    }
    
    public abstract String getMessage();
    
    public int getParamNo() {
        return paramNo;
    }

    
    public void setParamNo(int paramNo) {
        this.paramNo = paramNo;
    }

    public abstract boolean check(Order o);
    
    public String getErrorMessage(Order o) {
        return String.format(getMessage(), o.getParameter(getParamNo()));
    }
}
