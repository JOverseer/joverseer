package org.joverseer;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.joverseer.domain.Order;
import org.joverseer.support.Container;
import org.joverseer.support.ContainerCache;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContainerTest {
	org.joverseer.domain.Character c;
	org.joverseer.domain.Character c2;
	Order o1;
	Order o2;
	Order o3;

	@Before
	public void setUp() throws Exception {
		// create a character with 2 orders
		this.c = new org.joverseer.domain.Character();
		this.c.setName("char1");
		this.o1 = new Order(this.c);
		this.o1.setOrderNo(125);
		this.o2 = new Order(this.c);
		this.o2.setOrderNo(520);
		this.c2 = new org.joverseer.domain.Character();
		this.c2.setName("char2");
		this.o3 = new Order(this.c2);
		this.o3.setOrderNo(215);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Container<OrderResult> cont = new Container<OrderResult>();
		// check default is no caches.
		assertEquals(0, cont.caches.entrySet().size());
		assertEquals(0, cont.size());

		// add one order result to the container.
		OrderResult or = new OrderResult(this.o1,"msg1",OrderResultTypeEnum.Okay);
		assertFalse(cont.contains(or));
		cont.addItem(or);
		assertTrue(cont.contains(or));
		assertEquals(1, cont.size());
		// nothing cached so
		assertEquals(0, cont.caches.entrySet().size());

		//try a search
		OrderResult ans = cont.findFirstByProperty("order", this.o1);
		assertEquals(or,ans);
		//check that nothing was added to the cache.
		assertEquals(0, cont.caches.entrySet().size());

		// add another order result
		or = new OrderResult(this.o2,"msg2",OrderResultTypeEnum.Help);
		assertFalse(cont.contains(or));
		cont.addItem(or);
		assertTrue(cont.contains(or));
		assertEquals(2, cont.size());
		//check that we can get it, and that it isn't the first order result
		ans = cont.findFirstByProperty("order", this.o2);
		assertEquals(or,ans);

		//iterate over the container
		//sequence is not defined.
		int found = 0;
		for (OrderResult r: cont) {
			if (r.getMessage().equals("msg1")) {
				found = found | 1;
			} else if (r.getMessage().equals("msg2")) {
				found = found | 2;
			} else {
				assertFalse("unexpected item", true);
			}
		}
		// expect to find both.
		assertEquals(2 | 1, found);

		// add a result from a different character.
		or = new OrderResult(this.o3,"msg3",OrderResultTypeEnum.Okay);
		cont.addItem(or);

		// find the two orders from the same character.
		ArrayList<OrderResult> res = cont.findAllByProperty("order.character.name", "char1");
		assertEquals(2, res.size());

		//
		res = cont.findAllByProperties(new String[] {"type"}, new Object[] {OrderResultTypeEnum.Okay});
		assertEquals(2, res.size());

		res = cont.findAllByProperties(new String[] {"type","order.character.name"}, new Object[] {OrderResultTypeEnum.Okay,"char1"});
		assertEquals(1, res.size());

		// remove only those two orders for the same character.
		cont.removeAllByProperties("order.character.name", "char1");
		assertEquals(1,cont.size());

		res = cont.getItems();
		assertEquals("msg3", res.get(0).getMessage());

		res.clear();
		assertEquals(0, cont.size());
	}
	@Test
	public void testCaching() {
		Container<OrderResult> cont = new Container<OrderResult>(new String[] {"message","order","order.character.name"});
		assertEquals(3, cont.caches.entrySet().size());
		assertEquals(0, cont.size());

		OrderResult or = new OrderResult(this.o1,"msg1",OrderResultTypeEnum.Okay);
		cont.addItem(or);

		ArrayList<OrderResult> res = cont.findAllByProperty("order.character.name", "char1");
		assertEquals(1, res.size());

		or = new OrderResult(this.o3,"msg4",OrderResultTypeEnum.Okay);
		cont.addItem(or);

		// change the character!
		or.getOrder().setCharacter(this.c);

		// now the cache is stale, but doesn't know it.
		res = cont.findAllByProperty("order.character.name", "char1");
		assertEquals(1, res.size());

		cont.refreshItem(or);

		// now we get the correct answer.
		res = cont.findAllByProperty("order.character.name", "char1");
		assertEquals(2, res.size());
	}
}
