package org.joverseer.ui.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.domain.Army;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.orderEditor.OrderEditorAutoNations;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Commands that opens the OrderEditorAutoNationsForm and allows the user to set the
 * Order Editor Auto Nations (nations for which the character viewer automatically shows
 * the orders so they can be edited more easily - save one click to open the orders)
 * 
 * @author Marios Skounakis
 */
public class SetOrderEditorAutoNationsCommand extends ActionCommand {
    
    public SetOrderEditorAutoNationsCommand() {
        super("setOrderEditorAutoNationsCommand");
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final OrderEditorAutoNationsForm frm = new OrderEditorAutoNationsForm(FormModelHelper.createFormModel(new Army()));
        FormBackedDialogPage pg = new FormBackedDialogPage(frm);
        TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
            @Override
			protected boolean onFinish() {
                frm.commit();
                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.instance().getApplicationContext().getBean("messageSource");
        dlg.setTitle(ms.getMessage("orderEditorAutoNationsDialog.title", new Object[]{}, Locale.getDefault()));
        dlg.showDialog();
    }
    
    class OrderEditorAutoNationsForm extends AbstractForm {
        HashMap<Integer, JCheckBox> checkBoxes = new HashMap<Integer, JCheckBox>();
        
        
        public OrderEditorAutoNationsForm(FormModel arg0) {
            super(arg0, "orderEditorAutoNationsForm");
        }

        @Override
		protected JComponent createFormControl() {
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            final Game g = GameHolder.instance().getGame();
            for (int i=1; i<=25; i++) {
                Nation n = g.getMetadata().getNationByNum(i);
                if (n != null) {
                    JCheckBox box = new JCheckBox();
                    this.checkBoxes.put(i, box);
                    box.setSelected(OrderEditorAutoNations.instance().containsNation(i));
                    tlb.cell(new JLabel(n.getName()));
                    tlb.gapCol();
                    tlb.cell(box);
                    tlb.relatedGapRow();
                }
            }
            
            JButton btn = new JButton("Select All");
            btn.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    for (JCheckBox box : OrderEditorAutoNationsForm.this.checkBoxes.values()) {
                        box.setSelected(true);
                    }
                }
            });
            tlb.cell(btn);
            tlb.relatedGapRow();
            
            btn = new JButton("Select Imported");
            btn.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    for (Integer n : OrderEditorAutoNationsForm.this.checkBoxes.keySet()) {
                        PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", n);
                        OrderEditorAutoNationsForm.this.checkBoxes.get(n).setSelected(pi!=null);
                    }
                }
            });
            tlb.cell(btn);
            tlb.relatedGapRow();
            
            btn = new JButton("Deselect All");
            btn.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    for (JCheckBox box : OrderEditorAutoNationsForm.this.checkBoxes.values()) {
                        box.setSelected(false);
                    }
                }
            });
            tlb.cell(btn);
            tlb.relatedGapRow();
            
            return tlb.getPanel();
        }

        @Override
		public void commit() {
            super.commit();
            for (Integer i : this.checkBoxes.keySet()) {
                if (this.checkBoxes.get(i).isSelected()) {
                    OrderEditorAutoNations.instance().addNation(i);
                } else {
                    OrderEditorAutoNations.instance().removeNation(i);
                }
            }
        }
    }
    
}
