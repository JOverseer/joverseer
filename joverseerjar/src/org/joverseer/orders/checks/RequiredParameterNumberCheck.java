package org.joverseer.orders.checks;

import org.joverseer.domain.Order;


public class RequiredParameterNumberCheck extends AbstractCheck {
    int parameterNumber;
    
    public int getParameterNumber() {
        return this.parameterNumber;
    }
    
    public void setParameterNumber(int parameterNumber) {
        this.parameterNumber = parameterNumber;
    }

    public RequiredParameterNumberCheck(int parameterNumber) {
        super();
        this.parameterNumber = parameterNumber;
    }

    @Override
	public boolean check(Order o) {
        return o.getParameters().split(" ").length == getParameterNumber();
    }

    @Override
	public String getMessage() {
        return "The required information for the order was not supplied.";
    }
    

}
