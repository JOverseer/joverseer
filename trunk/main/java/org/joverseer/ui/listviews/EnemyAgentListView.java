package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.EnemyAgentWrapper;
import org.joverseer.domain.Character;


public class EnemyAgentListView extends BaseItemListView {


    public EnemyAgentListView() {
        super(EnemyAgentTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{120, 64, 64, 240};
    }
    
    
    protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        table.setDefaultRenderer(Boolean.class, new JTable().getDefaultRenderer(Boolean.class));
        return comp;
    }

    protected void setItems() {
        Container thieves = new Container(new String[]{"name"});
        Game g = GameHolder.instance().getGame();
        if (Game.isInitialized(g)) {
            String[] types = new String[] {
                    "theft",
                    "theft",
                    "assas.",
                    "kidnap"
            };
            String[] prefixes = new String[]{
                    "^There are rumors of a theft attempt involving (.+) at .+",
                    "^There are rumors of a theft attempt involving (.+)\\.$",
                    "^There are rumors of an assassination attempt involving (.+) and .+",
                    "^There are rumors of a kidnap attempt involving (.+) and .+",
            };
            for (int i=0; i<=g.getMaxTurn(); i++) {
                Turn t = g.getTurn(i);
                
                if (t == null) continue;
                
                NationRelations gameNr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", g.getMetadata().getNationNo());
                
                for (NationMessage nm : (ArrayList<NationMessage>)t.getContainer(TurnElementsEnum.NationMessage).getItems()) {
                    String charName = null;
                    String repType = null;
                    for (int j=0; j<prefixes.length; j++) {
                        String prefix = prefixes[j];
                        Matcher m = Pattern.compile(prefix).matcher(nm.getMessage());
                        if (m.matches()) {
                            charName = m.group(1);
                            repType = types[j];
                            break;
                        }
                    }
                    if (charName != null) {
                        EnemyAgentWrapper thief = (EnemyAgentWrapper)thieves.findFirstByProperty("name", charName);
                        Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", charName);
                        if (c != null) {
                            if (c.getNationNo() > 0) {
                                NationRelations nr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", c.getNationNo());
                                if (nr.getAllegiance() == gameNr.getAllegiance()) {
                                    continue;
                                }
                            }
                        }
                        if (thief == null) {
                            thief = new EnemyAgentWrapper();
                            boolean startChar = g.getMetadata().getCharacters().findFirstByProperty("id", Character.getIdFromName(charName)) != null;
                            thief.setName(charName);
                            thief.setTurnNo(t.getTurnNo());
                            thief.addReport(repType + " " + t.getTurnNo());
                            thief.setStartChar(startChar);
                            thieves.addItem(thief);
                        } else {
                            thief.addReport(repType + " " + t.getTurnNo());
                        }
                    }
                }
            }
        
        }
        tableModel.setRows(thieves.getItems());
        tableModel.fireTableDataChanged();
    }
}
