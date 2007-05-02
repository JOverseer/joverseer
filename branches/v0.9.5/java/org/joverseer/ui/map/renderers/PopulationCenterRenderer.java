package org.joverseer.ui.map.renderers;

import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.*;


public class PopulationCenterRenderer extends ImageRenderer {
    MapMetadata mapMetadata = null;

    static Logger logger = Logger.getLogger(PopulationCenterRenderer.class);

    public boolean appliesTo(Object obj) {
        return PopulationCenter.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }



    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        PopulationCenter popCenter = (PopulationCenter)obj;

        BufferedImage fortImage = null;
        if (popCenter.getFortification() != FortificationSizeEnum.none) {
            fortImage = getImage(popCenter.getFortification().toString() + ".image");
        }


        Point hexCenter = new Point(x + mapMetadata.getHexSize() / 2 * mapMetadata.getGridCellWidth(),
                                    y + mapMetadata.getHexSize() / 2 * mapMetadata.getGridCellHeight());

        // docks
        if (popCenter.getHarbor() != HarborSizeEnum.none) {
            BufferedImage dockImage = getImage(popCenter.getHarbor().toString() + ".icon");
            g.drawImage(dockImage, x + 5, hexCenter.y, null); 
        }

        BufferedImage pcImage = null;
        
        String capital = popCenter.getCapital() ? ".capital" : "";
        pcImage = getImage(popCenter.getSize().toString() + capital + ".image");

        BufferedImage img = copyImage(pcImage);
        Color color1 = ColorPicker.getInstance().getColor1(popCenter.getNationNo());
        Color color2 = ColorPicker.getInstance().getColor2(popCenter.getNationNo());
        changeColor(img, Color.red, color1);
        changeColor(img, Color.black, color2);
        if (popCenter.getHidden()) {
            makeHidden(img, color1, color2);
        }
        if (fortImage != null) {
            g.drawImage(fortImage, hexCenter.x - fortImage.getWidth() / 2, hexCenter.y - fortImage.getHeight(null) + pcImage.getHeight(null) / 2 , null);
        }
        g.drawImage(img, hexCenter.x - pcImage.getWidth(null) / 2, hexCenter.y - pcImage.getHeight(null) / 2 , null);
        
    }

}
