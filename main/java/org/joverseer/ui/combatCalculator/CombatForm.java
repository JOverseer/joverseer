package org.joverseer.ui.combatCalculator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.joverseer.domain.ClimateEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jgoodies.forms.layout.RowSpec;


public class CombatForm extends AbstractForm {
    public static String FORM_ID = "combatForm";
    CombatArmyTableModel side1TableModel;
    CombatArmyTableModel side2TableModel;
    JTable side1Table;
    JTable side2Table;
    
    public CombatForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    protected JComponent createFormControl() {
        SwingBindingFactory sbf = (SwingBindingFactory)getBindingFactory();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        
        TableLayoutBuilder lb = new TableLayoutBuilder();
        lb.cell(new JLabel("Terrain :"), "colspec=left:80px");
        lb.gapCol();
        JComboBox cb = (JComboBox)sbf.createBoundComboBox("terrain", new ListListModel(Arrays.asList(HexTerrainEnum.values()))).getControl(); 
        lb.cell(cb, "colspec=left:120px");
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commit();
                runCombat();
            }
        });
        
        lb.relatedGapRow();

        lb.cell(new JLabel("Climate :"), "colspec=left:80px");
        lb.gapCol();
        cb = (JComboBox)sbf.createBoundComboBox("climate", new ListListModel(Arrays.asList(ClimateEnum.values()))).getControl();
        lb.cell(cb, "colspec=left:120px");
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commit();
                runCombat();
            }
        });
        
        lb.relatedGapRow();
        
        tlb.cell(lb.getPanel(), "colspan=2");
        tlb.relatedGapRow();

        MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource");
        side1TableModel = new CombatArmyTableModel(this, messageSource);
        side1Table = TableUtils.createStandardSortableTable(side1TableModel);
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(side1Table, side1TableModel.getColumnWidths());
        
        JScrollPane scp = new JScrollPane(side1Table);
        scp.setPreferredSize(new Dimension(560, 200));
        tlb.cell(scp);

        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico;
        JButton btn;
        lb = new TableLayoutBuilder();
        ico = new ImageIcon(imgSource.getImage("edit.image"));
        btn = new JButton(ico);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new EditSelectedArmyCommand(0).doExecuteCommand();
            }
        });
        lb.cell(btn, "colspec=left:30px");
        lb.relatedGapRow();
        
        ico = new ImageIcon(imgSource.getImage("add.icon"));
        btn = new JButton(ico);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new AddArmyCommand(0).doExecuteCommand();
            }
        });
        lb.cell(btn, "colspec=left:30px");
        lb.relatedGapRow();
        
        ico = new ImageIcon(imgSource.getImage("remove.icon"));
        btn = new JButton(ico);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new RemoveSelectedArmyCommand(0).doExecuteCommand();
            }
        });
        lb.cell(btn, "colspec=left:30px");
        lb.relatedGapRow();
        lb.cell(new JLabel(" "));
        lb.row();
        
        tlb.gapCol();
        tlb.cell(lb.getPanel(), "colspec=left:40px");
        
        tlb.relatedGapRow();
        
        side2TableModel = new CombatArmyTableModel(this, messageSource);
        side2Table = TableUtils.createStandardSortableTable(side2TableModel);
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(side2Table, side2TableModel.getColumnWidths());
        
        scp = new JScrollPane(side2Table);
        scp.setPreferredSize(new Dimension(560, 200));
        tlb.cell(scp);
        
        lb = new TableLayoutBuilder();
        ico = new ImageIcon(imgSource.getImage("edit.image"));
        btn = new JButton(ico);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new EditSelectedArmyCommand(1).doExecuteCommand();
            }
        });
        lb.cell(btn, "colspec=left:30px");
        lb.relatedGapRow();
        
        ico = new ImageIcon(imgSource.getImage("add.icon"));
        btn = new JButton(ico);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new AddArmyCommand(1).doExecuteCommand();
            }
        });
        lb.cell(btn, "colspec=left:30px");
        lb.relatedGapRow();
        
        ico = new ImageIcon(imgSource.getImage("remove.icon"));
        btn = new JButton(ico);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new RemoveSelectedArmyCommand(1).doExecuteCommand();
            }
        });
        lb.cell(btn, "colspec=left:30px");
        lb.relatedGapRow();
        lb.cell(new JLabel(" "));
        lb.row();
        
        tlb.gapCol();
        tlb.cell(lb.getPanel());
        
        return tlb.getPanel();
        
    }

    public void setFormObject(Object arg0) {
        super.setFormObject(arg0);
        refreshArmies();
    }
    
    protected void runCombat() {
        Combat c = (Combat)getFormObject();
        for (CombatArmy ca : c.getSide1()) {
            if (ca != null) ca.setLosses(0);
        }
        for (CombatArmy ca : c.getSide2()) {
            if (ca != null) ca.setLosses(0);
        }
        c.runBattle();
        side1TableModel.fireTableDataChanged();
        side2TableModel.fireTableDataChanged();
    }
    
    protected void refreshArmies() {
        Combat c = (Combat)getFormObject();
        ArrayList sa = new ArrayList();
        for (CombatArmy ca : c.getSide1()) {
            if (ca != null) sa.add(ca);
        }
        side1TableModel.setRows(sa);
        
        sa = new ArrayList();
        for (CombatArmy ca : c.getSide2()) {
            if (ca != null) sa.add(ca);
        }
        side2TableModel.setRows(sa);
        
        runCombat();
        
        side1TableModel.fireTableDataChanged();
        side2TableModel.fireTableDataChanged();
    }
    
    class AddArmyCommand extends ActionCommand {
        int side;
        
        public AddArmyCommand(int side) {
            super();
            this.side = side;
        }

        protected void doExecuteCommand() {
            CombatArmyTableModel sideTableModel;
            CombatArmy ca = new CombatArmy();
            Combat combat = (Combat)getFormObject();
            if (side == 0) {
                for (int i=0; i<combat.getSide1().length; i++) {
                    if (combat.getSide1()[i] == null) {
                        combat.getSide1()[i] = ca;
                        refreshArmies();
                        return;
                    }
                }
            } else {
                for (int i=0; i<combat.getSide2().length; i++) {
                    if (combat.getSide2()[i] == null) {
                        combat.getSide2()[i] = ca;
                        refreshArmies();
                        return;
                    }
                }
            }
        }
    }
    
    class RemoveSelectedArmyCommand extends ActionCommand {
        int side;
        
        public RemoveSelectedArmyCommand(int side) {
            super();
            this.side = side;
        }

        protected void doExecuteCommand() {
            final Combat combat = (Combat)getFormObject();
            int idx = -1;
            if (side == 0) {
                idx = side1Table.getSelectedRow();
            } else {
                idx = side2Table.getSelectedRow();
            }
            if (idx < 0) return;
            if (side == 0) {
                final int idx1 = idx;
                ConfirmationDialog md = new ConfirmationDialog("Remove army?",
                        "Remove select army from side 1?") {
                    protected void onConfirm() {
                        int idx = ((SortableTableModel)side1Table.getModel()).convertSortedIndexToDataIndex(idx1);
                        combat.getSide1()[idx] = null;
                        refreshArmies();
                    }
                };
                md.showDialog();
            } else {
                final int idx1 = idx;
                ConfirmationDialog md = new ConfirmationDialog("Remove army?",
                        "Remove select army from side 2?") {
                    protected void onConfirm() {
                        int idx = ((SortableTableModel)side2Table.getModel()).convertSortedIndexToDataIndex(idx1);
                        combat.getSide2()[idx] = null;
                        refreshArmies();
                    }
                };
                md.showDialog();
            }
        }
    }
    
    class EditSelectedArmyCommand extends ActionCommand {
        int side;

        public EditSelectedArmyCommand(int side) {
            super();
            this.side = side;
        }

        protected void doExecuteCommand() {
            int idx = -1;
            if (side == 0) {
                idx = side1Table.getSelectedRow();
            } else {
                idx = side2Table.getSelectedRow();
            }
            if (idx < 0) return;
            CombatArmy ca = null;
            if (side == 0) {
                ca = (CombatArmy)side1TableModel.getRow(((SortableTableModel)side1Table.getModel()).convertSortedIndexToDataIndex(idx));
            } else {
                ca = (CombatArmy)side2TableModel.getRow(((SortableTableModel)side2Table.getModel()).convertSortedIndexToDataIndex(idx));
            }
            if (ca == null) return;
            FormModel formModel = FormModelHelper.createFormModel(ca);
            final CombatArmyForm form = new CombatArmyForm(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(form);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                }

                protected boolean onFinish() {
                    form.commit();
                    refreshArmies();
                    return true;
                }
            };
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("editCharacter.title", new Object[]{}, Locale.getDefault()));
            dialog.showDialog();       
            
        }
        
        
    }
}
