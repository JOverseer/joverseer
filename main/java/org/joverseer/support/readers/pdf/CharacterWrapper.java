package org.joverseer.support.readers.pdf;

import java.util.ArrayList;
import org.joverseer.domain.Character;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

public class CharacterWrapper {
	String name;
	String orders;
	int hexNo;
        boolean assassinated;
        boolean cursed;
        boolean executed;
	String artifacts;
        
	ArrayList<OrderResult> orderResults = new ArrayList<OrderResult>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<OrderResult> getOrderResults() {
		return orderResults;
	}

	public void setOrderResults(ArrayList orderResults) {
		this.orderResults = (ArrayList<OrderResult>)orderResults;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}
	
	public void addOrderResult(OrderResult result) {
		orderResults.add(result);
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	
	public void updateCharacter(Character c) {
            c.setOrderResults(getOrders());
            // update artifacts
            int idx = getArtifacts().indexOf("√"); 
            if (idx > -1) {
                // there is an artifact in use
                int i = getArtifacts().lastIndexOf("#", idx);
                int j = getArtifacts().indexOf(" ", i);
                String no = getArtifacts().substring(i+1, j).trim();
                int artiNo = Integer.parseInt(no);
                c.setArtifactInUse(artiNo);
            }
        }

    
        public boolean getAssassinated() {
            return assassinated;
        }
    
        
        public void setAssassinatedOn() {
            this.assassinated = true;
        }
        
        public boolean getCursed() {
            return cursed;
        }
    
        
        public void setCursedOn() {
            this.cursed = true;
        }

        public boolean getExecuted() {
            return executed;
        }
    
        
        public void setExecutedOn() {
            this.executed = true;
        }

        
        public String getArtifacts() {
            return artifacts;
        }

        
        public void setArtifacts(String artifacts) {
            this.artifacts = artifacts;
        }
        
        

}
