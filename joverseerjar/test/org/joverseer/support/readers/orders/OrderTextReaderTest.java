package org.joverseer.support.readers.orders;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.junit.Test;

public class OrderTextReaderTest {

	@Test
	public final void test() {
		OrderTextReader otr;
		Game game;
		game = new Game();
		otr = new OrderTextReader(game);
		assertNotNull("failed to create text reader",otr);
		assertEquals(0,otr.lineResults.size());
		assertEquals(0,otr.orders);
		assertEquals(OrderTextReader.STANDARD_ORDER_TEXT,otr.getTextType());
		try {
			otr.parseOrders(new BufferedReader(new StringReader("")));
			assertEquals(0,otr.lineResults.size());
			otr.parseOrders(new BufferedReader(new StringReader("bla")));
			assertEquals(1,otr.lineResults.size());
			assertEquals("Order notes.",otr.lineResults.get(0));
			
			otr.parseOrders(new BufferedReader(new StringReader("BEGINMEAUTOINPUT")));
			assertEquals(1,otr.lineResults.size());
			assertEquals("Joverseer or Automagic orders found.",otr.lineResults.get(0));
			assertEquals(OrderTextReader.AUTOMAGIC_ORDER_TEXT,otr.textType);

			// and switch to a non automagic 
			otr.parseOrders(new BufferedReader(new StringReader("bla")));
			assertEquals(1,otr.lineResults.size());
			assertEquals("Order notes.",otr.lineResults.get(0));
			// but the format is still AUTOMAGIC . probably a bug .
			// so can't reliably reuse or caller checks that the type hasn't changed.
			// semantics are a bit too vague?
			assertEquals(OrderTextReader.AUTOMAGIC_ORDER_TEXT,otr.textType);
			
			otr = new OrderCheckerOrderTextReader(game);
			assertEquals(OrderTextReader.ORDERCHECKER_ORDER_TEXT,otr.textType);
			otr.parseOrders(new BufferedReader(new StringReader("bla")));
			assertEquals(1,otr.lineResults.size());
			assertEquals("Order notes.",otr.lineResults.get(0));

			otr = new AutoMagicOrderTextReader(game);
			assertEquals(OrderTextReader.AUTOMAGIC_ORDER_TEXT,otr.textType);
			otr.parseOrders(new BufferedReader(new StringReader("bla")));
			assertEquals(1,otr.lineResults.size());
			assertEquals("Order notes.",otr.lineResults.get(0));

			// end of sub-type testing.
			// start of character line and order line matching
			
			otr = new OrderTextReader(game);
			// normal stuff
			assertEquals("1234", otr.getCharacterLocationFromLine("(frodo) @ 1234"));
			assertEquals("frodo", otr.getCharacterNameFromLine("(frodo) @ 1234"));
			assertEquals("1234", otr.getCharacterLocationFromLine("(frodo) @ 12345"));

			// degenerate behaviour :)
			assertEquals("1234", otr.getCharacterLocationFromLine("@ 1234)("));
			assertEquals("fro@ ", otr.getCharacterNameFromLine("(fro@ )"));
			assertEquals("@ frododododo", otr.getCharacterNameFromLine("(@ frododododo)"));
			
			assertTrue(otr.isCharacterLine("1 (frodo) @ 1234"));
			assertTrue(otr.isOrderLine("123 order"));

			otr = new OrderCheckerOrderTextReader(game);
			assertEquals("1234", otr.getCharacterLocationFromLine("(frodo) @ 1234"));
			assertEquals("frodo", otr.getCharacterNameFromLine("(frodo,"));
			// arguable
			assertEquals("2345", otr.getCharacterLocationFromLine("(frodo) @ 12345"));

			assertTrue(otr.isCharacterLine("1 (frodo) @ 1234"));
			assertTrue(otr.isOrderLine("123 (order)"));
			
			otr = new AutoMagicOrderTextReader(game);
			assertNull(otr.getCharacterLocationFromLine("(frodo) @ 1234"));
			assertNull(otr.getCharacterNameFromLine("(frodo,"));
			assertFalse(otr.isCharacterLine("1 (frodo) @ 1234"));
			assertFalse(otr.isOrderLine("123 (order)"));

			// end of character and order line testing
			// check that we notice that the turn isn't set yet.
			otr = new OrderTextReader(game);
			otr.parseOrders(new BufferedReader(new StringReader("1 (frodo) @ 1234\n731,,")));
			assertEquals(3, otr.lineResults.size());
			assertEquals("Character line (char id: frodo).", otr.lineResults.get(0));
			assertEquals("Order notes.", otr.lineResults.get(1));
			assertEquals("No turn found", otr.lineResults.get(2));
			
			assertEquals(-1,game.getCurrentTurn());
			Container<Turn> ct = game.getTurns();
			assertNotNull(ct);
			Turn t = new Turn();
			Container<Character> cc = t.getCharacters(); 
			assertNotNull(cc);
			assertEquals(0, cc.size());
			Character c = new Character();
			c.setName("frodo");
			c.setId("frodo");
			cc.addItem(c);
			game.addTurn(t);
			
			// end of turn and character container setup
			
			otr.parseOrders(new BufferedReader(new StringReader("1 (bilbo) @ 1234\n731,,")));
			assertEquals(2, otr.lineResults.size());
			assertEquals("Character line (char id: bilbo). Character was not found in game.", otr.lineResults.get(0));
			assertEquals("Order notes.", otr.lineResults.get(1));

			otr.parseOrders(new BufferedReader(new StringReader("1 (frodo) @ 1234\n731,,")));
			assertEquals(2, otr.lineResults.size());
			assertEquals("Character line (char id: frodo). Character was found but at a different location - ignoring.", otr.lineResults.get(0));
			assertEquals("Order notes.", otr.lineResults.get(1));

			c.setHexNo(1234);
			otr.parseOrders(new BufferedReader(new StringReader("1 (frodo) @ 1234\n731 NamAgt")));
			assertEquals(2, otr.lineResults.size());
			assertEquals("Character line (char id: frodo).", otr.lineResults.get(0));
			assertEquals("Order line. Parsed order: 731. Order will be added to frodo.", otr.lineResults.get(1));

			otr.lineResults.clear();
			otr.readOrders(new BufferedReader(new StringReader("Frodo (frodo) @ 1234 (C40)\n"+ "728  NamComm")));
			assertEquals(2, otr.lineResults.size());
			assertEquals("Character line (char id: frodo).", otr.lineResults.get(0));
			assertEquals("Order line. Parsed order: 728. Order will be added to frodo.", otr.lineResults.get(1));

			otr.lineResults.clear();
			otr.readOrders(new BufferedReader(new StringReader("Frodo (frodo) @ 1234 (C40)\n"+ "728  NamComm  f")));
			assertEquals(2, otr.lineResults.size());
			assertEquals("Character line (char id: frodo).", otr.lineResults.get(0));
			assertEquals("Order line. Parsed order: 728. Order will be added to frodo.", otr.lineResults.get(1));
			
			
			//TODO fix actual parsing of the order.
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
