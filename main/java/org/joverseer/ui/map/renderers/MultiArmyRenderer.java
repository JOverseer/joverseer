package org.joverseer.ui.map.renderers;

import org.joverseer.ui.map.MapMetadata;
import org.joverseer.domain.*;
import org.joverseer.game.Game;
import org.joverseer.ui.map.ColorPicker;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 6 Δεκ 2006
 * Time: 10:55:11 μμ
 * To change this template use File | Settings | File Templates.
 */
public class MultiArmyRenderer implements Renderer {
    MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return Army.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    private boolean isArmyFp(Army army) {
        //todo make decision based on allegiance, not nation no
        return army.getNationNo() <= 10;
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        Army army = (Army)obj;
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn turn = game.getTurn();

        ArrayList<Army> armiesInHex = turn.getContainer(TurnElementsEnum.Army).findAllByProperty("hexNo", army.getHexNo());

        boolean isArmyFp = isArmyFp(army);
        // find index of army in armiesInHex of same allegiance
        int i = 0;
        int j = 0;
        for (Army a : armiesInHex) {
            if (isArmyFp(a) == isArmyFp) {
                if (a == army) break;
                i++;
            }
        }
        // render up to five armies
        if (i >= 3) {
            j = 1;
            i = i - 3;
            if (i > 2) {
                return;
            }
        }

        int w = mapMetadata.getGridCellWidth() / 2;
        int h = mapMetadata.getGridCellHeight() / 2;
        int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 1 / 5;
        int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 7 / 10 + h * j;

        if (isArmyFp(army)) {
            dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 - (w + 1) * (i + 1) - 2;
        } else {
            dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() / 2 + (w + 1) * i + 2;
        }
        Color color1 = ColorPicker.getInstance().getColor1(army.getNationNo());
        Color color2 = ColorPicker.getInstance().getColor2(army.getNationNo());
        g.setColor(color1);
        g.fillRect(x + dx, y + dy, w, h);
        g.setColor(color2);
        g.drawRect(x + dx, y + dy, w, h);
    }
}
