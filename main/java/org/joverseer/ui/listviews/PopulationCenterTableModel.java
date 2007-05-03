package org.joverseer.ui.listviews;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.support.infoSources.InfoSource;
import org.springframework.context.MessageSource;


public class PopulationCenterTableModel extends ItemTableModel {
    static int iProductStart = 8;
    static int iLostThisTurn = 7;
    
    public PopulationCenterTableModel(MessageSource messageSource) {
        super(PopulationCenter.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"hexNo", "name", "nationNo", "size", "fortification", "loyalty", "infoSource", "lostThisTurn", "le", "br", "st", "mi", "fo", "ti", "mo", "go"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, String.class, String.class, String.class, String.class, Integer.class, InfoSource.class, String.class,
                               Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i >= iProductStart) {
            PopulationCenter pc = (PopulationCenter)object;
            ProductEnum pe = ProductEnum.getFromCode(createColumnPropertyNames()[i]);
            int s = pc.getStores(pe) == null ? 0 : pc.getStores(pe);
            int p = pc.getProduction(pe) == null ? 0 : pc.getProduction(pe);
            return s + p == 0 ? null : s + p;
        }
        if (i == iLostThisTurn) {
            PopulationCenter pc = (PopulationCenter)object;
            return pc.getLostThisTurn() ? "yes" : "";
        }
        return super.getValueAtInternal(object, i);
    }
    
    
}
