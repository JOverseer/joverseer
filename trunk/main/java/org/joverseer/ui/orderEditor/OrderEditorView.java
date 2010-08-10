package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.joverseer.domain.Order;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * View for the OrderEditor form
 * 
 * @author Marios Skounakis
 */
public class OrderEditorView extends AbstractView implements ApplicationListener{
    Form f;
    
    protected JComponent createControl() {
        f = (Form)Application.instance().getApplicationContext().getBean("orderEditor");
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(f.getControl(), "align=left");
        JPanel p = tlb.getPanel();
        p.setBackground(Color.white);
        JScrollPane scp = new JScrollPane(p);
        scp.setPreferredSize(new Dimension(800, 1000));
        scp.getVerticalScrollBar().setUnitIncrement(32);
        return scp;
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.EditOrderEvent.toString())) {
                GraphicUtils.showView("orderEditorView");
                ((OrderEditor)f).giveFocus();
            }
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                ((OrderEditor)f).setFormObject(new Order(null));
            }
            else if (e.getEventType().equals(LifecycleEventsEnum.RefreshMapItems.toString())) {
            	if (e.getSender() != f) {
            		((OrderEditor)f).refreshDrawCheck();
            	}
            }
        }
    }
}
