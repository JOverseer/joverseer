package org.joverseer.ui.command;

import java.util.ArrayList;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;


public class HighlightDragonsCommand  extends ActionCommand {
    
    public HighlightDragonsCommand() {
        super("highlightDragonsCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;

        HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Container chars = game.getTurn().getContainer(TurnElementsEnum.Character);
        for (Character c : (ArrayList<Character>)chars.getItems()) {
            if (InfoUtils.isDragon(c.getName())) {
                hhmi.addHex(c.getHexNo());
            }
        }
        AbstractMapItem.add(hhmi);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hhmi, this));
    }


}
