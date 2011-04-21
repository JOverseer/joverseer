package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

/**
 * Renderer for armies Renders one icon for all armies of the same allegiance in
 * the hex using the standard old and generic army icons (i.e. no army type)
 * 
 * @author Marios Skounakis
 */
public class ArmyIconRenderer extends ImageRenderer {
	MapMetadata mapMetadata = null;

	static Logger logger = Logger.getLogger(PopulationCenterRenderer.class);

	public boolean appliesTo(Object obj) {
		String pval = PreferenceRegistry.instance().getPreferenceValue("map.charsAndArmies");
		if (!pval.equals("simplified"))
			return false;
		return Army.class.isInstance(obj);
	}

	private void init() {
		mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
	}

	public void render(Object obj, Graphics2D g, int x, int y) {
		if (mapMetadata == null)
			init();

		Army army = (Army) obj;

		BufferedImage armyImage = null;
		// todo calculate from army allegiance
		NationAllegianceEnum allegiance = NationAllegianceEnum.Neutral;
		if (allegiance == NationAllegianceEnum.Neutral) {
			if (army.getNationNo() > 10) {
				allegiance = NationAllegianceEnum.DarkServants;
			} else {
				allegiance = NationAllegianceEnum.FreePeople;
			}
		}
		armyImage = getImage("army." + allegiance.toString() + ".image");

		BufferedImage img = copyImage(armyImage);
		Color color1 = ColorPicker.getInstance().getColor1(army.getNationNo());
		Color color2 = ColorPicker.getInstance().getColor2(army.getNationNo());
		if (allegiance == NationAllegianceEnum.FreePeople) {
			changeColor(img, Color.red, color1);
			changeColor(img, Color.black, color2);
		} else {
			changeColor(img, Color.red, color2);
			changeColor(img, Color.black, color1);
		}

		int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 5 / 20;
		int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 13 / 20;
		int w = armyImage.getWidth();
		// todo make decision based on allegiance, not nation no
		if (army.getNationNo() > 10) {
			dx = dx + mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - w;
		} else {
			dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - dx;
		}

		g.drawImage(img, x + dx, y + dy, null);

	}
}
