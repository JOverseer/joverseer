package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.springframework.binding.form.FormModel;
import org.springframework.core.io.Resource;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Credits form
 * 
 * @author Marios Skounakis
 */
public class CreditsForm extends AbstractForm implements HyperlinkListener {
    public static String PAGE_NAME = "CreditsForm";
    
    JEditorPane editor;
    
    public CreditsForm(FormModel arg0) {
        super(arg0, PAGE_NAME);
    }

    @Override
	protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        this.editor = new JEditorPane();
        this.editor.setEditable(false);
        this.editor.setPreferredSize(new Dimension(750, 500));
        try {
            this.editor.setPage(((Resource)getFormObject()).getURL());
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        JScrollPane scp = new JScrollPane(this.editor); 
        scp.setPreferredSize(new Dimension(750, 500));
        glb.append(scp);
        glb.nextLine();
        return glb.getPanel();
    }

    @Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
        try {
            this.editor.setPage(arg0.getURL());
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }
    
    
    

}
