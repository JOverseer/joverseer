package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.support.drawing.ColorPicker;

/**
 * Renderer for armies Renders one icon for all armies of the same allegiance in
 * the hex using the standard old and generic army icons (i.e. no army type)
 * 
 * @author Marios Skounakis
 */
public class ArmyIconRenderer extends ImageRenderer {
	private boolean simplified=true;

	@SuppressWarnings("hiding")
	static Logger logger = Logger.getLogger(ArmyIconRenderer.class);

	@Override
	public boolean appliesTo(Object obj) {
		if (!this.simplified)
			return false;
		return Army.class.isInstance(obj);
	}

	@Override
	public void render(Object obj, Graphics2D g, int x, int y) {
		Army army = (Army) obj;

		BufferedImage armyImage = null;
		NationAllegianceEnum allegiance = army.getNationAllegiance();
		
        String pval2 = PreferenceRegistry.instance().getPreferenceValue("map.armySize");
        double mod = Double.parseDouble(pval2)/10;
		
		armyImage = getImage("army." + allegiance.toString() + ".image", 1.0 + mod);

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

		int dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() * 5 / 20;
		int dy = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() * 13 / 20;
		int w = armyImage.getWidth();
		switch (allegiance) {
		case FreePeople:
			dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2 - dx;
			break;
		case DarkServants:
			dx = dx + this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2 - w;
			break;
		case Neutral:
			// fall thru
		default:
			dx = dx + this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2;
			break;
		
		}

		g.drawImage(img, x + dx, y + dy, null);

	}

	@Override
	public void refreshConfig() {
		String pval = PreferenceRegistry.instance().getPreferenceValue("map.charsAndArmies");
		this.simplified = pval.equals("simplified");
	}
}
