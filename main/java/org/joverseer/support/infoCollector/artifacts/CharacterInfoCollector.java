package org.joverseer.support.infoCollector.artifacts;

import java.util.HashMap;

import org.joverseer.support.Container;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class CharacterInfoCollector implements ApplicationListener {
	HashMap<Integer, Container> turnInfos = new HashMap<Integer, Container>();
	
	
	public void onApplicationEvent(ApplicationEvent arg0) {
	}
	
	
}
