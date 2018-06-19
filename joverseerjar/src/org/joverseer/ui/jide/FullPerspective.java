package org.joverseer.ui.jide;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.perspective.Perspective;

/**
 * The basic perspective for the application
 * 
 * @author Marios Skounakis
 */
public class FullPerspective extends Perspective {

	public FullPerspective() {
		super();
	}

	public FullPerspective(String id) {
		super(id);
	}

	@Override
	public void display(DockingManager manager) {
		manager.showFrame("mapView");
		manager.showFrame("mapOptionsView");
		manager.showFrame("currentHexDataViewer");
		manager.showFrame("characterListView");
		manager.showFrame("populationCenterListView");
		manager.showFrame("nationEconomyListView");
		manager.showFrame("orderEditorView");
		manager.showFrame("economyCalculatorView");
		manager.showWorkspace();
		
	}

}
