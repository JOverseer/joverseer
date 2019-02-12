package org.joverseer.ui.orderEditor.test;

import java.util.ArrayList;

import javax.swing.JComponent;

import junit.framework.TestCase;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.ui.orderEditor.OrderEditor;
import org.joverseer.ui.orderEditor.OrderEditorData;

/**
 * Tests whether the OrderEditor can support all parameter types specified
 * in the OrderEditorData specified in the respective metadata file
 *
 * @author Marios Skounakis
 */
public class TestOrderParameters extends TestCase {
		OrderEditor theOrderEditor;
	public TestOrderParameters(OrderEditor orderEditor) {
		this.theOrderEditor = orderEditor;
	}
    public void testOrderParametersForOrderEditor() {
        OrderEditor oe = this.theOrderEditor;
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
