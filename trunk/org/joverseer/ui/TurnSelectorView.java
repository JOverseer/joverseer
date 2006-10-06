package org.joverseer.ui;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.events.GameChangedListener;
import org.joverseer.ui.events.GameChangedEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 3:09:50 μμ
 * To change this template use File | Settings | File Templates.
 */
public class TurnSelectorView extends AbstractView implements ApplicationListener {
    JComboBox cmbTurns;

    protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("Turn : "));
        tlb.cell(cmbTurns = new JComboBox());
        resetGame();

        JPanel panel = tlb.getPanel();
        panel.setPreferredSize(new Dimension(150, 150));
        return panel;
    }

    public void resetGame() {
        cmbTurns.removeAllItems();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g != null) {
            for (int i=0; i<g.getMaxTurn(); i++) {
                if (g.getTurn(i) != null) {
                    cmbTurns.addItem(i);
                }
            }
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof LifecycleApplicationEvent) {
            LifecycleApplicationEvent e = (LifecycleApplicationEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                resetGame();
            }
        }
    }
}
