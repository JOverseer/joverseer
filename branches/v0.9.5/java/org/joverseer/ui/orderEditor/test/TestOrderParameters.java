package org.joverseer.ui.orderEditor.test;

import java.util.ArrayList;

import javax.swing.JComponent;

import org.joverseer.domain.Order;
import org.joverseer.domain.Character;
import org.joverseer.ui.orderEditor.OrderEditor;
import org.joverseer.ui.orderEditor.OrderEditorData;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.ApplicationDialog;

import junit.framework.TestCase;


public class TestOrderParameters extends TestCase {
    public void testOrderParametersForOrderEditor() {
        OrderEditor oe = (OrderEditor)Application.instance().getApplicationContext().getBean("orderEditor");
        Character c = new Character();
        c.setCommand(50);
        c.setCommandTotal(50);
        c.setAgent(50);
        c.setAgentTotal(50);
        c.setMage(50);
        c.setMageTotal(50);
        c.setEmmisary(50);
        c.setEmmisaryTotal(50);
        for (OrderEditorData oed : (ArrayList<OrderEditorData>)oe.getOrderEditorData().getItems()) {
            if (oed.getOrderNo() == 850 || oed.getOrderNo() == 860 || oed.getOrderNo() == 830) continue;
            Order o = new Order(c);
            o.setOrderNo(oed.getOrderNo());
            oe.setFormObject(o);
            ArrayList<String> ptypes = new ArrayList<String>();
            for (int i=0; i<oed.getParamTypes().size(); i++) {
                if (oed.getParamTypes().get(i) != null && !oed.getParamTypes().get(i).equals("")) {
                    ptypes.add(oed.getParamTypes().get(i));
                }
            }
            ArrayList<JComponent> comps = new ArrayList<JComponent>();
            for (int i=0; i<oe.getSubeditorComponents().size(); i++) {
                if (oe.getSubeditorComponents().get(i) != null) {
                    comps.add(oe.getSubeditorComponents().get(i));
                }
            }
            if (comps.size() != ptypes.size()) {
                System.out.println("Problem with order " + oed.getOrderNo() + " " + oed.getOrderDescr());
            }
        }
    }
}
