package org.joverseer.ui.listviews;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationEconomy;
import org.springframework.context.MessageSource;


public class NationEconomyPotentialTableModel extends ItemTableModel {

    public NationEconomyPotentialTableModel(MessageSource messageSource) {
        super(NationEconomyPotential.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"nationNo", "surplus", "reserve", "total", "oneNatSell", "twoNatSells", "charsInCapital"};  
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class,  String.class, String.class, String.class, String.class}; 
    }
    
    public NationEconomyPotential getNewPotential() {
        return new NationEconomyPotential();
    }

    public class NationEconomyPotential implements IBelongsToNation, IHasMapLocation {
        Integer nationNo;
        int hexNo;
        int surplus;
        int reserve;
        int total;
        int oneNatSell;
        int twoNatSells;
        int charsInCapital;
        
        public NationEconomyPotential() {};
        
        public int getCharsInCapital() {
            return charsInCapital;
        }
        
        public void setCharsInCapital(int charsInCapital) {
            this.charsInCapital = charsInCapital;
        }
        
        public Integer getNationNo() {
            return nationNo;
        }
        
        public void setNationNo(Integer nationNo) {
            this.nationNo = nationNo;
        }
        
        public int getOneNatSell() {
            return oneNatSell;
        }
        
        public void setOneNatSell(int oneNatSell) {
            this.oneNatSell = oneNatSell;
        }
        
        public int getReserve() {
            return reserve;
        }
        
        public void setReserve(int reserve) {
            this.reserve = reserve;
        }
        
        public int getSurplus() {
            return surplus;
        }
        
        public void setSurplus(int surplus) {
            this.surplus = surplus;
        }
        
        public int getTotal() {
            return total;
        }
        
        public void setTotal(int total) {
            this.total = total;
        }
        
        public int getTwoNatSells() {
            return twoNatSells;
        }
        
        public void setTwoNatSells(int twoNatSells) {
            this.twoNatSells = twoNatSells;
        }

        
        public int getHexNo() {
            return hexNo;
        }

        
        public void setHexNo(int hexNo) {
            this.hexNo = hexNo;
        }

        public int getX() {
            return getHexNo() / 100;
        }

        public int getY() {
            return getHexNo() % 100;
        }
        
        
    }
}
