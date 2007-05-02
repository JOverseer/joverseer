package org.joverseer.ui;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.springframework.binding.form.FormModel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class CreditsForm extends AbstractForm implements HyperlinkListener {
    public static String PAGE_NAME = "CreditsForm";
    
    JEditorPane editor;
    
    public CreditsForm(FormModel arg0) {
        super(arg0, PAGE_NAME);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        editor = new JEditorPane();
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(750, 500));
        try {
            editor.setPage(((Resource)getFormObject()).getURL());
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        glb.append(new JScrollPane(editor));
        glb.nextLine();
        return glb.getPanel();
    }

    public void hyperlinkUpdate(HyperlinkEvent arg0) {
        try {
            editor.setPage(arg0.getURL());
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }
    
    
    

}
