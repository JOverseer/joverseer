package org.joverseer.ui.economyCalculator;

import java.util.ArrayList;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.BeanTableModel;


public class LostPopsTableModel extends BeanTableModel {
    int selectedNationNo = -1;
    
    public LostPopsTableModel() {
        super(PopulationCenter.class, (MessageSource) Application.instance().getApplicationContext().getBean("messageSource"));
        setRowNumbers(false);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {
                "name", 
                "hexNo",
                "size",
                "fortification",
                "loyalty",
                "lostThisTurn"};
    }
    
    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, Boolean.class};
    }

    
    public int getSelectedNationNo() {
        return selectedNationNo;
    }

    
    public void setSelectedNationNo(int selectedNationNo) {
        this.selectedNationNo = selectedNationNo;
    }

    protected boolean isCellEditableInternal(Object arg0, int arg1) {
        return arg1 == 5;
    }

    protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
        super.setValueAtInternal(arg0, arg1, arg2);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.EconomyCalculatorUpdate.toString(), this, this));

    }
    
    
}
