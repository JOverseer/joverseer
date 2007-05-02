package org.joverseer.ui.support.dialogs;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class InputDialog extends TitledPageApplicationDialog {
    ArrayList<String> componentNames = new ArrayList<String>();
    ArrayList<JComponent> components = new ArrayList<JComponent>();
    String descr;
    boolean result = false;
    
    public InputDialog() {
        super();
    }
    
    public void addComponent(String name, JComponent component) {
        componentNames.add(name);
        components.add(component);
    }
    
    public JComponent getComponent(String name) {
        for (int i=0; i<componentNames.size(); i++) {
            if (componentNames.get(i).equals(name)) {
                return components.get(i);
            }
        }
        return null;
    }
    
    public void init(String description) {
        InputDialogForm frm = new InputDialogForm(this);
        setDialogPage(new FormBackedDialogPage(frm));
        setPreferredSize(new Dimension(400, 300));
        descr = description;
    }



    class InputDialogForm extends AbstractForm {
        InputDialog dlg;
        
        public InputDialogForm(InputDialog dlg) {
            super(FormModelHelper.createFormModel(new Object()), "INPUT_DIALOG_FORM");
            this.dlg = dlg;
        }

        protected JComponent createFormControl() {
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            for (int i=0; i<componentNames.size(); i++) {
                tlb.cell(new JLabel(componentNames.get(i)));
                tlb.gapCol();
                tlb.cell(components.get(i));
                tlb.relatedGapRow();
            }
            return tlb.getPanel();
        }
    }


    protected boolean onFinish() {
        result = true;
        return true;
    }

    protected void onAboutToShow() {
        super.onAboutToShow();
        setDescription(descr);
    }

    public boolean getResult() {
        return result;
    }
    
}
