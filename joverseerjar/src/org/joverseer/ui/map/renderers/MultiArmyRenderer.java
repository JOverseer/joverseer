package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapTooltipHolder;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

/**
 * Renders multiple armies as seperate icons within the hex.
 * Can show a specific image per army type (cav, inf, nav, unknown)
 * Up to 10 armies can be shown (5 for each side)
 * Neutral armies are shown in the opposing allegiance's side
 * 
 * @author Marios Skounakis
 */
public class MultiArmyRenderer extends ImageRenderer {
    MapMetadata mapMetadata = null;
    
    @Override
	public boolean appliesTo(Object obj) {
        String pval = PreferenceRegistry.instance().getPreferenceValue("map.charsAndArmies");
        if (pval.equals("simplified")) return false;
        return Army.class.isInstance(obj);
    }

    private void init() {
        this.mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    private boolean isArmyFp(Army army) {
//        //todo make decision based on allegiance, not nation no
//        return army.getNationNo() <= 10;
        return (army.getNationAllegiance() == NationAllegianceEnum.FreePeople);
    }

    @Override
	public void render(Object obj, Graphics2D g, int x, int y) {
        if (this.mapMetadata == null) init();

        Army army = (Army)obj;
        
        boolean showAnchoredShips =
            PreferenceRegistry.instance().getPreferenceValue("map.anchoredShips").equals("yes");
        if (!showAnchoredShips && Army.isAnchoredShips(army)) return;
        
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn turn = game.getTurn();

        ArrayList<Army> armiesInHex = turn.getContainer(TurnElementsEnum.Army).findAllByProperty("hexNo", army.getHexNo());

        boolean isArmyFp = isArmyFp(army);
        // find index of army in armiesInHex of same allegiance
        int i = 0;
        int j = 0;
        for (Army a : armiesInHex) {
            if (!showAnchoredShips && Army.isAnchoredShips(a)) continue;
            if (isArmyFp(a) == isArmyFp) {
                if (a == army) break;
                i++;
            }
        }
        int maxFirstLine = this.mapMetadata.getGridCellWidth() < 10 ? 2 : 3;
        int maxSecondLine = this.mapMetadata.getGridCellWidth() < 10 ? 1 : 2;
        // render up to five armies
        if (i >= maxFirstLine) {
            j = 1;
            i = i - maxFirstLine;
            if (i >= maxSecondLine) {
                return;
            }
        }

        String type = "army";
        String info = "unknown";
        
        String pval = PreferenceRegistry.instance().getPreferenceValue("map.showArmyType");
        if (pval.equals("yes")) {
            Boolean cav = army.computeCavalry();
            info = cav == null ? "unknown" : cav == false ? "inf" : "cav";
            
            Boolean navy = army.isNavy();
            type = navy == null ? "army" : navy ? "navy" : "army";
            if (army.isNavy() && Army.isAnchoredShips(army)) {
                info = "anchored";
            }
        }
        BufferedImage armyImage = null;
        NationAllegianceEnum allegiance = army.getNationAllegiance();
//        if (allegiance == NationAllegianceEnum.Neutral) {
//            //todo pick game's nation opposite allegiance
//            NationRelations nr = (NationRelations)game.getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", game.getMetadata().getNationNo());
//            if (nr != null) {
//                if (nr.getAllegiance() == NationAllegianceEnum.FreePeople) {
//                    allegiance = NationAllegianceEnum.DarkServants;
//                } else if (nr.getAllegiance() == NationAllegianceEnum.DarkServants) {
//                    allegiance = NationAllegianceEnum.FreePeople;
//                }
//            } else {
//                allegiance = NationAllegianceEnum.DarkServants;
//            }
//        }
        armyImage = getImage(type + "." + info + "." + allegiance.toString() + ".image");

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


        int w = 9;
        int h = 9;
        int dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() * 1 / 5;
        int dy = this.mapMetadata.getGridCellHeight() * this.mapMetadata.getHexSize() * 13 / 20 + h * j;

        if (isArmyFp(army)) {
            dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2 - (w) * (i + 1) - 1;
        } else {
            dx = this.mapMetadata.getGridCellWidth() * this.mapMetadata.getHexSize() / 2 + (w) * i + 1;
        }
        g.drawImage(img, x + dx, y + dy, null);
        MapTooltipHolder.instance().addTooltipObject(new Rectangle(x + dx, y+dy, img.getWidth(), img.getHeight()), army);
    }
}
