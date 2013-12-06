package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.infoSources.spells.DerivedFromDivCharWithForcesInfoSource;


public class DivCharsWithForcesResultWrapper implements OrderResult {
    String commander;
    String characters;
    
    public String getCharacters() {
        return this.characters;
    }
    
    public void setCharacters(String characters) {
        this.characters = characters;
    }
    
    public String getCommander() {
        return this.commander;
    }
    
    public void setCommander(String commander) {
        this.commander = commander;
    }
    
    @Override
	public void updateGame(Game game, Turn turn, int nationNo, String casterName) {
        if (getCommander() == null || getCharacters() == null) return;
        // get army
        Army a = (Army)turn.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", getCommander());
        if (a == null) return;
        //get list of chars
        String[] chars = getCharacters().split("-");
        for (String c : chars) {
            DerivedFromDivCharWithForcesInfoSource is = new DerivedFromDivCharWithForcesInfoSource(turn.getTurnNo(), nationNo, casterName);
            c = c.trim();
            Character ch = (Character)turn.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", c);
            if (ch != null) {
                ch.setHexNo(Integer.parseInt(a.getHexNo()));
            } else {
                ch = new Character();
                ch.setName(c);
                ch.setId(Character.getIdFromName(c));
                ch.setHexNo(Integer.parseInt(a.getHexNo()));
                ch.setInfoSource(is);
                ch.setNationNo(a.getNationNo());
                turn.getContainer(TurnElementsEnum.Character).addItem(ch);
            }
        }
    }
}
