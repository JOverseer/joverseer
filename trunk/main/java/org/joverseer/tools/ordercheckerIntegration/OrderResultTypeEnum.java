package org.joverseer.tools.ordercheckerIntegration;


public enum OrderResultTypeEnum {
    Okay(-1),
    Info(0),
    Help(1),
    Warning(2),
    Error(3);
    
    int value;

    private OrderResultTypeEnum(int value) {
        this.value = value;
    }

    
    public int getValue() {
        return value;
    }
    
    
}
