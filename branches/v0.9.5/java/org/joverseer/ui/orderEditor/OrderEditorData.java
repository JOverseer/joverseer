package org.joverseer.ui.orderEditor;

import java.util.ArrayList;


public class OrderEditorData {
    int orderNo;
    String orderDescr;
    String orderType;
    String parameterDescription;
    ArrayList<String> paramTypes = new ArrayList<String>();
    ArrayList<String> paramDescriptions = new ArrayList<String>();
    String majorSkill;
    String skill;
    
    public String getMajorSkill() {
        return majorSkill;
    }
    
    public void setMajorSkill(String majorSkill) {
        this.majorSkill = majorSkill;
    }
    
    public String getOrderDescr() {
        return orderDescr;
    }
    
    public void setOrderDescr(String orderDescr) {
        this.orderDescr = orderDescr;
    }
    
    public int getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public String getParameterDescription() {
        return parameterDescription;
    }
    
    public void setParameterDescription(String parameterDescription) {
        this.parameterDescription = parameterDescription;
    }
    
    public ArrayList<String> getParamTypes() {
        return paramTypes;
    }
    
    public void setParamTypes(ArrayList<String> paramTypes) {
        this.paramTypes = paramTypes;
    }
    
    public String getSkill() {
        return skill;
    }
    
    public void setSkill(String skill) {
        this.skill = skill;
    }

	protected ArrayList<String> getParamDescriptions() {
		return paramDescriptions;
	}

	protected void setParamDescriptions(ArrayList<String> paramDescriptions) {
		this.paramDescriptions = paramDescriptions;
	}
    
    
}
