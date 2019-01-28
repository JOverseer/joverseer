package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;
import org.joverseer.ui.support.dialogs.ErrorDialog;
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

    /**
     * @wbp.parser.entryPoint
     */
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
        	Logger.getRootLogger().error(exc.getMessage());
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
        	Logger.getRootLogger().error(exc.getMessage());
        }
    }
    
    
    

}
