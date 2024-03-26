package org.joverseer.tools.combatCalc;

import static org.junit.Assert.*;

import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
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

public class CombatTest {

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
		ctx.refresh();
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
	public final void test() {
		CombatPopCenter cpc = new CombatPopCenter();
		assertEquals("fortification",0, cpc.fort.ordinal());
		assertEquals("size",3, cpc.size.ordinal());
		assertEquals(50, cpc.loyalty);
		Combat combat = new Combat();
		int PCstrength = combat.computePopCenterStrength(cpc);
		assertEquals(1500, PCstrength);
		cpc.setLoyalty(12);
		cpc.setFort(FortificationSizeEnum.none);
		cpc.setSize(PopulationCenterSizeEnum.camp);
		PCstrength = combat.computePopCenterStrength(cpc);
		assertEquals(224, PCstrength);
		CombatArmy ca = new CombatArmy();

		// requires InfoRegistry to be set.
		Application app = new Application();
		Application.load(app);
		
		assertEquals(0, Combat.computeNativeArmyStrength(ca, HexTerrainEnum.plains, ClimateEnum.Cool, true));
	}
}
