package org.joverseer.htmlExport;

import java.awt.image.BufferedImage;
import java.io.File;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.ui.map.MapPanel;


public class GameExporter {
    public void ExportGame(Game g) {
        
    }
    
    public BufferedImage GetTurnMapImage(Game g, int turnNo) {
        MapPanel mp = new MapPanel();
        mp.setGame(g);
        g.setCurrentTurn(turnNo);
        mp.invalidateAll();
        BufferedImage map = mp.getMapImage();
        return map;
    }
    
    public static void main(String[] args) throws Exception {
        GameExporter e = new GameExporter();
        Game g = Game.loadGame(new File("C:\\Documents and Settings\\mskounak\\My Documents\\game26.jov"));
        e.GetTurnMapImage(g, g.getCurrentTurn());
    }
}
