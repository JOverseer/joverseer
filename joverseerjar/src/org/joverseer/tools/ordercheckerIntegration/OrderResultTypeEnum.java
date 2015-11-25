package org.joverseer.tools.ordercheckerIntegration;

/**
 * Order Result Type enumeration
 * 
 * @author Marios Skounakis
 */
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
        return this.value;
    }
    
    
}
