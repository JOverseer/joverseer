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
import org.joverseer.domain.NationMessage;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.SimpleLifecycleAdvisor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.viewers.PopulationCenterViewer;
import org.joverseer.ui.viewers.CharacterViewer;
import org.joverseer.ui.viewers.ArmyViewer;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.game.Turn;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.GameMetadata;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 27 ��� 2006
 * Time: 10:02:19 ��
 * To change this template use File | Settings | File Templates.
 */
public class CurrentHexDataViewer extends AbstractView implements ApplicationListener {
    JPanel panel;
    JPanel mainPanel;
    JPanel popCenterPanel;
    PopulationCenterViewer popCenterViewer;
    JPanel hexInfoPanel;
    HexInfoViewer hexInfoViewer;
    ArrayList<CharacterViewer> characterViewers = new ArrayList<CharacterViewer>();
    ArrayList<JPanel> characterPanels = new ArrayList<JPanel>();
    ArrayList<ArmyViewer> armyViewers = new ArrayList<ArmyViewer>();
    ArrayList<JPanel> armyPanels = new ArrayList<JPanel>();
    ArrayList<NationMessageViewer> nationMessageViewers = new ArrayList<NationMessageViewer>();
    ArrayList<JPanel> nationMessagePanels = new ArrayList<JPanel>();
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

        tlb.separator(" Nation Messages ");
        tlb.row();
        for (int i=0; i<20; i++) {
            NationMessageViewer nmv = new NationMessageViewer(FormModelHelper.createFormModel(new NationMessage()));
            nationMessageViewers.add(nmv);
            JPanel cp = new JPanel();
            cp.add(nmv.getControl());
            cp.setBackground(Color.white);
            nationMessagePanels.add(cp);
            tlb.cell(cp, "align=left");
            tlb.row();
            cp.setVisible(false);
        }

        hexInfoViewer = new HexInfoViewer(FormModelHelper.createFormModel(new Hex()));
        hexInfoPanel = new JPanel();
        hexInfoPanel.add(hexInfoViewer.getControl());
        tlb.separator(" Hex Info ");
        tlb.row();
        tlb.cell(hexInfoPanel, "align=left");
        tlb.row();
        hexInfoPanel.setVisible(false);
        hexInfoPanel.setBackground(Color.white);

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

    private void showHexInfo(Hex h) {
        hexInfoViewer.setFormObject(h);
        hexInfoPanel.setVisible(true);
    }

    private void hideHexInfo() {
        hexInfoPanel.setVisible(false);
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

    private void showNationMessage(NationMessage c) {
        for (int i=0; i<nationMessageViewers.size(); i++) {
            if (!nationMessagePanels.get(i).isVisible()) {
                nationMessageViewers.get(i).setFormObject(c);
                nationMessagePanels.get(i).setVisible(true);
                return;
            }
        }
    }

    private void hideAllCharacterViewers() {
        for (int i=0; i<characterViewers.size(); i++) {
            characterPanels.get(i).setVisible(false);
        }

    }

    private void hideAllNationMessageViewers() {
        for (int i=0; i<nationMessageViewers.size(); i++) {
            nationMessagePanels.get(i).setVisible(false);
        }

    }

    private void hideAllArmyViewers() {
        for (int i=0; i<armyPanels.size(); i++) {
            armyPanels.get(i).setVisible(false);
        }

    }

    private void refresh(Point p) {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null) return;
        GameMetadata gm = g.getMetadata();
        Hex h = gm.getHex(p.x * 100 + p.y);
        if (h != null) {
            showHexInfo(h);
        } else {
            hideHexInfo();
        }
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
        ArrayList chars = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        BeanComparator comp = new BeanComparator("name");
        Collections.sort(chars, comp);

        for (Character ch : (Collection<Character>)chars) {
            showCharacter(ch);
        }

        hideAllArmyViewers();
        c = t.getContainer(TurnElementsEnum.Army);
        ArrayList armies = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        comp = new BeanComparator("nationAllegiance");
        Collections.sort(armies, comp);

        for (Army a : (Collection<Army>)armies) {
            showArmy(a);
        }

        hideAllNationMessageViewers();
        c = t.getContainer(TurnElementsEnum.NationMessage);
        Collection items = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        for (NationMessage obj : (Collection<NationMessage>)items) {
            showNationMessage(obj);
        }

    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                Point p = (Point)e.getObject();
                refresh(p);
            }
        }
    }
}
