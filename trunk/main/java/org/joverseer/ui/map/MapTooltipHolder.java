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
		if (toolTipObjects == null) {
			initToolTipObjects();
		}
		for (int i = rectangle.x; i <= rectangle.x + rectangle.width; i++) {
			for (int j = rectangle.y; j <= rectangle.y + rectangle.height; j++) {
				toolTipObjects[i][j] = obj;
			}
		}
	}

	public void showTooltip(Point mapLocation, Point screenLocation) {
		try {
			if (toolTipObjects == null) {
				initToolTipObjects();
			}
			Object obj = toolTipObjects[mapLocation.x][mapLocation.y];
			String tooltipText = null;
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
			if ((tooltipText == null && currentToolTipText != null) || !tooltipText.equals(currentToolTipText)) {
				currentToolTipText = tooltipText;
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
		toolTipObjects = new Object[d.width][d.height];
	}

	public void reset() {
		toolTipObjects = null;
	}

}
