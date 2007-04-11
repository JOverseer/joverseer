package org.joverseer.ui.views;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;



public class EditArmyForm extends AbstractForm {
    public static String FORM_ID = "editArmyForm";

    JTextField commander;
    JTextField commandRank;
    JTextField morale;
    JTextField food;
    JTable elements;
    BeanTableModel elementTableModel;
    
    public EditArmyForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
        
        lb.append(new JLabel("Commander"));
        lb.append(commander = new JTextField());
        commander.setPreferredSize(new Dimension(120, 20));
        
        lb.nextLine();
        
        lb.append(new JLabel("Command Rank"));
        lb.append(commandRank = new JTextField());
        commandRank.setPreferredSize(new Dimension(60, 20));
        
        lb.nextLine();

        lb.append(new JLabel("Morale"));
        lb.append(morale = new JTextField());
        morale.setPreferredSize(new Dimension(60, 20));

        lb.nextLine();

        lb.append(new JLabel("Food"));
        lb.append(food = new JTextField());
        food.setPreferredSize(new Dimension(60, 20));

        lb.nextLine();
        
        lb.append(new JLabel("Elements"));
        lb.nextLine();
        JComponent panel = lb.getPanel();
        
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        JPanel pnl = new JPanel();
        pnl.add(panel);
        tlb.cell(pnl, "align=left");
        tlb.relatedGapRow();
        
        elements = new JTable(elementTableModel = new ArmyElementTableModel((MessageSource)Application.instance().getApplicationContext().getBean("messageSource")));
        elements.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if ((Integer)value == 0) {
                    value = null;
                } 
                JLabel lbl = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.RIGHT);
                return lbl;
            }
        });
        JScrollPane scp = new JScrollPane(elements);
        scp.setPreferredSize(new Dimension(300, 180));
        tlb.cell(scp);
        
        return new JScrollPane(tlb.getPanel());
    }
    
    
    
    public void setFormObject(Object arg0) {
        super.setFormObject(arg0);
        Army a = (Army)arg0;
        
        commander.setText(a.getCommanderName());
        
        Character c = (Character)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", a.getCommanderName());
        if (c != null) {
            commandRank.setText(String.valueOf(c.getCommandTotal()));
        }
        morale.setText(String.valueOf(a.getMorale()));
        food.setText(String.valueOf(a.getFood()));
        
        ArrayList<ArmyElement> elementList = new ArrayList<ArmyElement>();
        for (ArmyElementType aet : ArmyElementType.values()) {
            ArmyElement nae = new ArmyElement(aet, 0);
            ArmyElement ae = a.getElement(aet);
            if (ae != null) {
                nae.setNumber(ae.getNumber());
                nae.setTraining(ae.getTraining());
                nae.setWeapons(ae.getWeapons());
                nae.setArmor(ae.getArmor());
            }
            elementList.add(nae);
        }
        elementTableModel.setRows(elementList);
        TableUtils.sizeColumnsToFitRowData(elements);
    }



    class ArmyElementTableModel extends BeanTableModel {
        
        public ArmyElementTableModel(MessageSource ms) {
            super(ArmyElement.class, ms);
            setRowNumbers(false);
        }

        protected String[] createColumnPropertyNames() {
            return new String[]{"armyElementType.type", "number", "training", "weapons", "armor"};
        }

        protected Class[] createColumnClasses() {
            return new Class[]{String.class, Integer.class, Integer.class, Integer.class, Integer.class};
        }
        
    }
    
    

}
