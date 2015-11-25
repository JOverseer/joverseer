package org.joverseer.ui.orderEditor;

import java.util.ArrayList;

/**
 * Holds information about editing Orders - i.e. information for the order parameters
 * Data is loaded from file orderEditorData.csv
 * @author Marios Skounakis
 */
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
        return this.majorSkill;
    }
    
    public void setMajorSkill(String majorSkill) {
        this.majorSkill = majorSkill;
    }
    
    public String getOrderDescr() {
        return this.orderDescr;
    }
    
    public void setOrderDescr(String orderDescr) {
        this.orderDescr = orderDescr;
    }
    
    public int getOrderNo() {
        return this.orderNo;
    }
    
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getOrderType() {
        return this.orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public String getParameterDescription() {
        return this.parameterDescription;
    }
    
    public void setParameterDescription(String parameterDescription) {
        this.parameterDescription = parameterDescription;
    }
    
    public ArrayList<String> getParamTypes() {
        return this.paramTypes;
    }
    
    public void setParamTypes(ArrayList<String> paramTypes) {
        this.paramTypes = paramTypes;
    }
    
    public String getSkill() {
        return this.skill;
    }
    
    public void setSkill(String skill) {
        this.skill = skill;
    }

    public ArrayList<String> getParamDescriptions() {
    	return this.paramDescriptions;
    }
    
    public void setParamDescriptions(ArrayList<String> paramDescriptions) {
    	this.paramDescriptions = paramDescriptions;
    }
    
    
}
