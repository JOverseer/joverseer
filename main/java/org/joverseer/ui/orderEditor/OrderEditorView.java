package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.joverseer.domain.Order;
import org.joverseer.domain.Character;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class OrderEditorView extends AbstractView {
    Form f;
    
    protected JComponent createControl() {
        f = (Form)Application.instance().getApplicationContext().getBean("orderEditor");
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(f.getControl(), "align=left");
        JPanel p = tlb.getPanel();
        p.setBackground(Color.white);
        JScrollPane scp = new JScrollPane(p);
        scp.setPreferredSize(new Dimension(240, 1000));
        scp.getVerticalScrollBar().setUnitIncrement(32);
        return scp;
    }
    

}
