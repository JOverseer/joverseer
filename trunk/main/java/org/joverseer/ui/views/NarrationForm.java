package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.joverseer.ui.support.GraphicUtils;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Form that shows a generic narration (e.g. a combat narration or an encounter)
 * 
 * @author Marios Skounakis
 */
public class NarrationForm extends AbstractForm {
    
    public static final String FORM_PAGE = "narrationForm";

    JTextArea textArea;
    
    public NarrationForm(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    @Override
	protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.textArea.setFont(GraphicUtils.getFont("Courier New", Font.PLAIN, 11));
        tlb.cell(this.textArea);
        JScrollPane scp = new JScrollPane(this.textArea);
        scp.setPreferredSize(new Dimension(820, 450));
        scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scp;
    }
    
    @Override
	public void setFormObject(Object obj) {
        super.setFormObject(obj);
        this.textArea.setText((String)obj);
        this.textArea.setCaretPosition(0);
    }
}
