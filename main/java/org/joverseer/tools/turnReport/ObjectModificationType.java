package org.joverseer.tools.turnReport;

public enum ObjectModificationType {
	Gained (1),
	Modified (2),
	Lost (3);
	
	int value;

	ObjectModificationType(int value) {
       this.value = value;
    }

    public int getValue() {
        return value;
    }
}
