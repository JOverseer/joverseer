package org.joverseer.ui.support.dialogs;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.ui.views.Messages;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Standardized generic input dialog that can have an arbitrary number of input components
 * which are defined externally using the addComponent method
 * 
 * @author Marios Skounakis
 */
//TODO should change to use an overridable method to get the components

public class InputDialog extends TitledPageApplicationDialog {
    ArrayList<String> componentNames = new ArrayList<String>();
    ArrayList<JComponent> components = new ArrayList<JComponent>();
    String descr;
    boolean result = false;
    
    public InputDialog() {
        super();
        this.setImage(new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY));
    }
    public InputDialog(String titleId) {
        this();
        setTitle(Messages.getString(titleId));
        this.setImage(new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY));
    }
    
    public void addComponent(String name, JComponent component) {
        this.componentNames.add(name);
        this.components.add(component);
    }
    
    public JComponent getComponent(String name) {
        for (int i=0; i<this.componentNames.size(); i++) {
            if (this.componentNames.get(i).equals(name)) {
                return this.components.get(i);
            }
        }
        return null;
    }
    
    public void init(String description) {
        InputDialogForm frm = new InputDialogForm(this);
        setDialogPage(new FormBackedDialogPage(frm));
        setPreferredSize(new Dimension(450, 300));
        this.descr = description;
    }



    class InputDialogForm extends AbstractForm {
        InputDialog dlg;
        
        public InputDialogForm(InputDialog dlg) {
            super(FormModelHelper.createFormModel(new Object()), "INPUT_DIALOG_FORM");
            this.dlg = dlg;
        }

        @Override
		protected JComponent createFormControl() {
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            for (int i=0; i<InputDialog.this.componentNames.size(); i++) {
                tlb.cell(new JLabel(InputDialog.this.componentNames.get(i)));
                tlb.gapCol();
                tlb.cell(InputDialog.this.components.get(i));
                tlb.relatedGapRow();
            }
            return tlb.getPanel();
        }
    }


    @Override
	protected boolean onFinish() {
        this.result = true;
        return true;
    }

    @Override
	protected void onAboutToShow() {
        super.onAboutToShow();
        setDescription(this.descr);
    }

    public boolean getResult() {
        return this.result;
    }
    
}
