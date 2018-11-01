package org.joverseer;

import static org.junit.Assert.assertNotNull;
import org.joverseer.ui.JOverseerJIDEClient;
import org.junit.Test;
import org.springframework.richclient.application.Application;

public class LaunchTest {
	@Test
	public void simpleTest() {
		JOverseerJIDEClient.launchTestFramework();
		Application app = Application.instance();
		assertNotNull(app);
	}
	
}
