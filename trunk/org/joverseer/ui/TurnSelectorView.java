package org.joverseer.ui;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.flexdock.FlexDockApplicationPageFactory;
import org.springframework.richclient.application.flexdock.FlexDockApplicationPage;
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
import org.joverseer.ui.support.JOverseerEvent;
import org.flexdock.docking.DockingManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 3:09:50 μμ
 * To change this template use File | Settings | File Templates.
 */
public class TurnSelectorView extends AbstractView implements ApplicationListener, ActionListener {
    JComboBox cmbTurns;

    protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("Turn : "));
        tlb.cell(cmbTurns = new JComboBox());
        resetGame();
        tlb.row();
        JButton btn;
        tlb.cell(btn = new JButton());
        btn.setText("test");
        btn.addActionListener(this);

        JPanel panel = tlb.getPanel();
        panel.setPreferredSize(new Dimension(150, 150));
        return panel;
    }

    public void resetGame() {
        cmbTurns.removeAllItems();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g != null) {
            for (int i=0; i<=g.getMaxTurn(); i++) {
                if (g.getTurn(i) != null) {
                    cmbTurns.addItem(g.getTurn(i).getTurnNo());
                }
            }
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                resetGame();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = cmbTurns.getSelectedItem();
        int turnNo = (Integer)obj;

        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        g.setCurrentTurn(turnNo);

        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));

    }
}
