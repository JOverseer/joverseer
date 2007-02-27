package org.joverseer.ui.command;

import java.util.ArrayList;
import java.util.Locale;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;


public class HighlightDegradeCandidates extends ActionCommand {
    int loyaltyThreshold = 17;
    
    public HighlightDegradeCandidates() {
        super("highlightDegradeCandidatesCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;

        HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Container pcs = game.getTurn().getContainer(TurnElementsEnum.PopulationCenter);
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)pcs.getItems()) {
            if (pc.getLoyalty() > 0 && pc.getLoyalty() < getLoyaltyThreshold()) {
                hhmi.addHex(pc.getHexNo());
            }
        }
        AbstractMapItem.add(hhmi);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hhmi, this));
    }

    
    public int getLoyaltyThreshold() {
        return loyaltyThreshold;
    }

    
    public void setLoyaltyThreshold(int loyaltyThreshold) {
        this.loyaltyThreshold = loyaltyThreshold;
    }
    
    
}
