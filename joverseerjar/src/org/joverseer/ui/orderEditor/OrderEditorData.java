package org.joverseer.ui.orderEditor;

import java.io.BufferedReader;
import java.util.ArrayList;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;

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
    
	static public Container<OrderEditorData> CreateOrderEditorData(String fileName) {
		Container<OrderEditorData> container = new Container<OrderEditorData>(new String[] { "orderNo" }); //$NON-NLS-1$
		try {
			GameMetadata gm1 = GameMetadata.instance();
			BufferedReader reader = gm1.getUTF8Resource(fileName); 

			String ln;
			while ((ln = reader.readLine()) != null) {
				try {
					String[] partsL = ln.split(";"); //$NON-NLS-1$
					String[] parts = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
					for (int i = 0; i < partsL.length; i++) {
						parts[i] = partsL[i];
					}
					OrderEditorData oed = new OrderEditorData();
					oed.setOrderNo(Integer.parseInt(parts[0]));
					oed.setOrderDescr(parts[1]);
					oed.setParameterDescription(parts[2]);
					oed.setOrderType(parts[3]);
					oed.getParamTypes().add(parts[4]);
					oed.getParamTypes().add(parts[5]);
					oed.getParamTypes().add(parts[6]);
					oed.getParamTypes().add(parts[7]);
					oed.getParamTypes().add(parts[8]);
					oed.getParamTypes().add(parts[9]);
					oed.getParamTypes().add(parts[10]);
					oed.setMajorSkill(parts[11]);
					oed.setSkill(parts[12]);
					container.addItem(oed);
					ln = reader.readLine();
					if (ln == null) {
						ln = ""; //$NON-NLS-1$
					}
					partsL = ln.split(";"); //$NON-NLS-1$
					parts = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
					for (int i = 0; i < partsL.length; i++) {
						parts[i] = partsL[i];
					}
					oed.getParamDescriptions().add(parts[4]);
					oed.getParamDescriptions().add(parts[5]);
					oed.getParamDescriptions().add(parts[6]);
					oed.getParamDescriptions().add(parts[7]);
					oed.getParamDescriptions().add(parts[8]);
					oed.getParamDescriptions().add(parts[9]);
					oed.getParamDescriptions().add(parts[10]);
				} catch (Exception exc) {
					System.out.println(ln);
				}
			}
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
			container = null;
		}
		return container;
	}
    
}
