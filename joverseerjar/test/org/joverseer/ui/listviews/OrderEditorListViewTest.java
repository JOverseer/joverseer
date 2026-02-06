package org.joverseer.ui.listviews;

import static org.junit.Assert.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.NationReader;
import org.joverseer.support.GameHolder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.table.ShuttleSortableTableModel;

public class OrderEditorListViewTest extends AssertJSwingJUnitTestCase {

	@BeforeClass
	public static void onlyOnce() {
		GenericApplicationContext ctx = new GenericApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
		xmlReader.loadBeanDefinitions(new ClassPathResource("/org/joverseer/ui/listviews/testAppContext.xml"));
		Application app = new Application();
		Application.load(app);
		app.setApplicationContext(ctx);
		assertNotNull(Application.instance());
		assertTrue(Application.isLoaded());
		//ctx.refresh();		THROWING ERROR FOR SOME REASON NOW?
		assertNotNull(Application.instance().getApplicationContext().getBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME));
		ApplicationServicesLocator asl = new ApplicationServicesLocator();
		ApplicationServices as = new DefaultApplicationServices(ctx);
		assertEquals(asl, ApplicationServicesLocator.instance());
		assertTrue(as.containsService(ComponentFactory.class));
		assertNotNull(as.getService(ComponentFactory.class));
		asl.setApplicationServices(as);
		assertNotNull(ApplicationServicesLocator.services());
//		String[] names = ctx.getBeanDefinitionNames();

	}


	@Test
	public void constructor() {
		OrderEditorListView oelv = GuiActionRunner.execute(() -> new OrderEditorListView());
		assertNotNull(oelv.filters);
		assertEquals(0, oelv.filters.size());
		assertNull(oelv.table);
	 	JComponent comp = GuiActionRunner.execute(() -> oelv.createControlImpl());
	 	assertNotNull(oelv.table);
		assertEquals("JPanel",comp.getClass().getSimpleName());
		assertEquals(0,oelv.table.getRowCount());
		assertEquals(23,oelv.table.getColumnCount());
		TableModel tm = oelv.table.getModel();
		assertNotNull(tm);
		assertEquals("ShuttleSortableTableModel", tm.getClass().getSimpleName());
		ShuttleSortableTableModel stm = (ShuttleSortableTableModel) tm;
		assertEquals("OrderEditorTableModel",stm.getFilteredModel().getClass().getSimpleName());
		assertNull(oelv.table.getName());
	}
	@Test
	public void singleOrder() throws Exception {
		OrderEditorListView oelv = (OrderEditorListView)Application.instance().getApplicationContext().getBean("orderListView");
	 	JComponent comp = GuiActionRunner.execute(() -> oelv.createControlImpl());
	 	assertNotNull(oelv.table);
	 	OrderEditorTableModel oetm =  (OrderEditorTableModel)((ShuttleSortableTableModel)oelv.table.getModel()).getFilteredModel();
	 	GameHolder gh = JOApplication.getGameHolder();
	 	Game game = new Game();
	 	Turn turn = new Turn();
	 	turn.setTurnNo(0);
	 	GameMetadata gm = new GameMetadata();
	 	gm.setGameType(GameTypeEnum.game1650);
	 	NationReader nr = new NationReader();
	 	nr.load(gm);
	 	game.setMetadata(gm);
	 	assertNotNull(game.getMetadata());
	 	org.joverseer.support.Container<Character> chars = turn.getCharacters();
	 	org.joverseer.domain.Character c = new Character();
	 	c.setName("frodo");
	 	c.setHexNo(810);
	 	chars.addItem(c);
	 	game.addTurn(turn);
	 	gh.setGame(game);
	 	c.setNationNo(1);
	 	Order order = c.getOrders()[0];
//	 	order.setNationNo(1);
	 	GuiActionRunner.execute(() -> oetm.addRow(order));
	 	assertEquals("String",oetm.getValueAt(0, 0).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 1).getClass().getSimpleName());
	 	assertEquals("Integer",oetm.getValueAt(0, 2).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 4).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 5).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 6).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 7).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 8).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 9).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 10).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 11).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 12).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 13).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 14).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 15).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 16).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 17).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 18).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 19).getClass().getSimpleName());
	 	assertEquals("Boolean",oetm.getValueAt(0, 20).getClass().getSimpleName()); // needs orderVisualizationData
	 	Object x = oetm.getValueAt(0, 21); // needs orderResultContainer
	 	assertNull(x);
	 	assertEquals("String",oetm.getValueAt(0, 22).getClass().getSimpleName());
	 	assertEquals("String",oetm.getValueAt(0, 23).getClass().getSimpleName());
	 	assertEquals("Woo", oetm.getValueAt(0, 0));
	 	assertEquals("frodo", oetm.getValueAt(0, 1));
	 	assertEquals(810, oetm.getValueAt(0, 2));
	 	assertEquals("", oetm.getValueAt(0, 3));
	 	assertEquals(" N/A", oetm.getValueAt(0, 4));
	 	assertEquals("", oetm.getValueAt(0, 5));
	 	assertEquals("", oetm.getValueAt(0, 6));
	 	assertEquals("", oetm.getValueAt(0, 7));
	 	assertEquals("", oetm.getValueAt(0, 8));
	 	assertEquals("", oetm.getValueAt(0, 9));
	 	assertEquals("", oetm.getValueAt(0, 10));
	 	assertEquals("", oetm.getValueAt(0, 11));
	 	assertEquals("", oetm.getValueAt(0, 12));
	 	assertEquals("", oetm.getValueAt(0, 13));
	 	assertEquals("", oetm.getValueAt(0, 14));
	 	assertEquals("", oetm.getValueAt(0, 15));
	 	assertEquals("", oetm.getValueAt(0, 16));
	 	assertEquals("", oetm.getValueAt(0, 17));
	 	assertEquals("", oetm.getValueAt(0, 18));
	 	assertEquals("", oetm.getValueAt(0, 19));
	 	assertEquals(false, oetm.getValueAt(0, 20));
	 	x = oetm.getValueAt(0, 21); 
	 	assertNull(x);
	 	assertEquals("", oetm.getValueAt(0, 22));
	 	assertEquals("", oetm.getValueAt(0, 23));

	 	JFrame frame = GuiActionRunner.execute(() -> new JFrame());
	    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

	    GuiActionRunner.execute(() -> {
	    	frame.setContentPane(comp);
	    	frame.setTitle("FrameUnderTest");
		    frame.pack();
		    frame.setVisible(true);
		 	oelv.table.setVisible(true);
		 	comp.invalidate();
		 	frame.invalidate();
	    }
	    );
/*
	 	FrameFixture asframe = new FrameFixture("FrameUnderTest");
//	 	asframe.show();
	 	JTableFixture jtf =  asframe.table();
	 	assertEquals(1, jtf.rowCount());
	 	asframe.cleanUp();
*/	}


	@Override
	protected void onSetUp() {
		// TODO Auto-generated method stub

	}

}
