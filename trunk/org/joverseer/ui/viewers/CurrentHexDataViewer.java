package org.joverseer.ui.viewers;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.Character;
import org.joverseer.domain.Army;
import org.joverseer.ui.events.SelectedHexChangedListener;
import org.joverseer.ui.events.SelectedHexChangedEvent;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.SimpleLifecycleAdvisor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.game.Turn;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 27 Σεπ 2006
 * Time: 10:02:19 μμ
 * To change this template use File | Settings | File Templates.
 */
public class CurrentHexDataViewer extends AbstractView implements ApplicationListener {
    JPanel panel;
    JPanel mainPanel;
    JPanel popCenterPanel;
    PopulationCenterViewer popCenterViewer;
    ArrayList<CharacterViewer> characterViewers = new ArrayList<CharacterViewer>();
    ArrayList<JPanel> characterPanels = new ArrayList<JPanel>();
    ArrayList<ArmyViewer> armyViewers = new ArrayList<ArmyViewer>();
    ArrayList<JPanel> armyPanels = new ArrayList<JPanel>();
    JScrollPane scp;

    protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        popCenterViewer = new PopulationCenterViewer(FormModelHelper.createFormModel(new PopulationCenter()));
        popCenterPanel = new JPanel();
        popCenterPanel.add(popCenterViewer.getControl());
        tlb.separator(" Population Center ");
        tlb.row();
        tlb.cell(popCenterPanel, "align=left");
        tlb.row();
        popCenterPanel.setVisible(false);
        popCenterPanel.setBackground(Color.white);

        tlb.separator(" Armies ");
        tlb.row();
        for (int i=0; i<20; i++) {
            ArmyViewer va = new ArmyViewer(FormModelHelper.createFormModel(new Army()));
            armyViewers.add(va);
            JPanel cp = new JPanel();
            cp.add(va.getControl());
            cp.setBackground(Color.white);
            armyPanels.add(cp);
            tlb.cell(cp, "align=left");
            tlb.row();
            cp.setVisible(false);
        }

        tlb.separator(" Characters ");
        tlb.row();
        for (int i=0; i<20; i++) {
            CharacterViewer vc = new CharacterViewer(FormModelHelper.createFormModel(new Character()));
            characterViewers.add(vc);
            JPanel cp = new JPanel();
            cp.add(vc.getControl());
            cp.setBackground(Color.white);
            characterPanels.add(cp);
            tlb.cell(cp, "align=left");
            tlb.row();
            cp.setVisible(false);
        }

        panel = tlb.getPanel();
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(240, 1000));
        JScrollPane scp = new JScrollPane(panel);
        scp.setPreferredSize(new Dimension(240, 1000));
        return scp;
    }



    private void showPopCenter(PopulationCenter pc) {
        popCenterViewer.setFormObject(pc);
        popCenterPanel.setVisible(true);
    }

    private void hidePopCenter() {
        popCenterPanel.setVisible(false);
    }

    private void showCharacter(Character c) {
        for (int i=0; i<characterViewers.size(); i++) {
            if (!characterPanels.get(i).isVisible()) {
                characterViewers.get(i).setFormObject(c);
                characterPanels.get(i).setVisible(true);
                return;
            }
        }
    }

    private void showArmy(Army a) {
        for (int i=0; i<armyViewers.size(); i++) {
            if (!armyPanels.get(i).isVisible()) {
                armyViewers.get(i).setFormObject(a);
                armyPanels.get(i).setVisible(true);
                return;
            }
        }
    }

    private void hideAllCharacterViewers() {
        for (int i=0; i<characterViewers.size(); i++) {
            characterPanels.get(i).setVisible(false);
        }

    }

    private void hideAllArmyViewers() {
        for (int i=0; i<armyPanels.size(); i++) {
            armyPanels.get(i).setVisible(false);
        }

    }

    public void eventOccured(SelectedHexChangedEvent ev) {
        Point p = ((MapPanel)ev.getSource()).getSelectedHex();
        refresh(p);
    }

    private void refresh(Point p) {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn t = g.getTurn();
        org.joverseer.support.Container c = t.getContainer(TurnElementsEnum.PopulationCenter);
        PopulationCenter pc = (PopulationCenter)c.findFirstByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        if (pc != null) {
            showPopCenter(pc);
        } else {
            hidePopCenter();
        }

        hideAllCharacterViewers();
        c = t.getContainer(TurnElementsEnum.Character);
        Collection chars = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        for (Character ch : (Collection<Character>)chars) {
            showCharacter(ch);
        }

        hideAllArmyViewers();
        c = t.getContainer(TurnElementsEnum.Army);
        Collection armies = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        for (Army a : (Collection<Army>)armies) {
            showArmy(a);
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof LifecycleApplicationEvent) {
            LifecycleApplicationEvent e = (LifecycleApplicationEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                Point p = (Point)e.getObject();
                refresh(p);
            }
        }
    }
}

