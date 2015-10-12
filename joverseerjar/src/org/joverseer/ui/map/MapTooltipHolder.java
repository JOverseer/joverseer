package org.joverseer.ui.map;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.springframework.richclient.application.Application;

public class MapTooltipHolder {
	Object[][] toolTipObjects = null;
	String currentToolTipText = null;

	private static MapTooltipHolder instance = null;

	public static MapTooltipHolder instance() {
		if (instance == null) {
			instance = (MapTooltipHolder) Application.instance().getApplicationContext().getBean("mapTooltipHolder");
		}
		return instance;
	}

	public void addTooltipObject(Rectangle rectangle, Object obj) {
		if (this.toolTipObjects == null) {
			initToolTipObjects();
		}
		for (int i = rectangle.x; i <= rectangle.x + rectangle.width; i++) {
			for (int j = rectangle.y; j <= rectangle.y + rectangle.height; j++) {
				this.toolTipObjects[i][j] = obj;
			}
		}
	}

	public void showTooltip(Point mapLocation, Point screenLocation) {
		try {
			if (this.toolTipObjects == null) {
				initToolTipObjects();
			}
			Object obj = this.toolTipObjects[mapLocation.x][mapLocation.y];
			String tooltipText = null;
			boolean doUpdate = false;
			
			if (obj == null) {
				tooltipText = null;
			} else {
				if (Army.class.isInstance(obj)) {
					tooltipText = ((Army) obj).getCommanderName();
				} else if (Character.class.isInstance(obj)) {
					tooltipText = ((Character) obj).getName();
				} else if (PopulationCenter.class.isInstance(obj)) {
					tooltipText = ((PopulationCenter) obj).getName();
				}
			}
			if (tooltipText == null) {
				doUpdate = (this.currentToolTipText != null);
			} else {
				doUpdate = (!tooltipText.equals(this.currentToolTipText));
			}
			if (doUpdate) {
				this.currentToolTipText = tooltipText;
				MapPanel.instance().setToolTipText(tooltipText);
				Action toolTipAction = MapPanel.instance().getActionMap().get("postTip");
				if (toolTipAction != null) {
					ActionEvent postTip = new ActionEvent(MapPanel.instance(), ActionEvent.ACTION_PERFORMED, "");
					toolTipAction.actionPerformed(postTip);
				}
			}
		} catch (Exception exc) {

		}
	}

	public void initToolTipObjects() {
		Dimension d = MapPanel.instance().getMapDimension();
		this.toolTipObjects = new Object[d.width][d.height];
	}

	public void reset() {
		this.toolTipObjects = null;
	}

}
