package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.beanutils.BeanComparator;
import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ArmyAllegianceNameComparator;
import org.joverseer.tools.CharacterDeathAllegianceNameComparator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.support.DefaultViewDescriptor;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.docking.LayoutManager;
import com.jidesoft.spring.richclient.docking.view.JideAbstractView;



/**
 * Shows information for the current hex, using the various viewers in this package
 * 
 * For some object types (e.g. pop center or hex info) only one viewer is created
 * For other object types (e.g. characters), multiple viewers are created 
 * 
 * Viewers are shown/hidden as needed
 * 
 * @author Marios Skounakis
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
    ArrayList<JPanel> artifactPanels = new ArrayList<JPanel>();
    ArrayList<ArtifactViewer> artifactViewers = new ArrayList<ArtifactViewer>();
    ArrayList<CombatViewer> combatViewers = new ArrayList<CombatViewer>();
    ArrayList<JPanel> combatPanels = new ArrayList<JPanel>();
    ArrayList<EncounterViewer> encounterViewers = new ArrayList<EncounterViewer>();
    ArrayList<JPanel> encounterPanels = new ArrayList<JPanel>();
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
        for (int i=0; i<50; i++) {
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
        
        tlb.separator(" Artifacts ");
        tlb.row();
        for (int i=0; i<20; i++) {
            ArtifactViewer av = new ArtifactViewer(FormModelHelper.createFormModel(new Artifact()));
            JPanel ap = new JPanel();
            artifactViewers.add(av);
            artifactPanels.add(ap);
            ap.add(av.getControl());
            ap.setBackground(Color.white);
            tlb.cell(ap, "align=left");
            tlb.row();
            ap.setVisible(false);
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
        
        tlb.separator(" Combats / Challenges / Encounters ");
        tlb.row();
        for (int i=0; i<5; i++) {
            CombatViewer cv = new CombatViewer(FormModelHelper.createFormModel(new Combat()));
            combatViewers.add(cv);
            JPanel cp = new JPanel();
            cp.add(cv.getControl());
            cp.setBackground(Color.white);
            combatPanels.add(cp);
            tlb.cell(cp, "align=left");
            tlb.row();
            cp.setVisible(false);
        }
        for (int i=0; i<5; i++) {
            EncounterViewer cv = new EncounterViewer(FormModelHelper.createFormModel(new Encounter()));
            encounterViewers.add(cv);
            JPanel cp = new JPanel();
            cp.add(cv.getControl());
            cp.setBackground(Color.white);
            encounterPanels.add(cp);
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
        panel.setPreferredSize(new Dimension(240, 3000));
        scp = new JScrollPane(panel);
        scp.setPreferredSize(new Dimension(240, 1500));
        scp.getVerticalScrollBar().setUnitIncrement(32);
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
    
    private void showArtifact(Artifact a) {
        for (int i=0; i<artifactPanels.size(); i++) {
            if (!artifactPanels.get(i).isVisible()) {
                artifactViewers.get(i).setFormObject(a);
                artifactPanels.get(i).setVisible(true);
                return;
            }
        }
    }
    
    public void showCombat(Combat c) {
        for (int i=0; i<combatPanels.size(); i++) {
            if (!combatPanels.get(i).isVisible()) {
                combatViewers.get(i).setFormObject(c);
                combatPanels.get(i).setVisible(true);
                return;
            }
        }
    }
    
    public void showEncounter(Encounter c) {
        for (int i=0; i<encounterPanels.size(); i++) {
            if (!encounterPanels.get(i).isVisible()) {
                encounterViewers.get(i).setFormObject(c);
                encounterPanels.get(i).setVisible(true);
                return;
            }
        }
    }

    private void hideAllCharacterViewers() {
        for (int i=0; i<characterViewers.size(); i++) {
            characterPanels.get(i).setVisible(false);
        }

    }
    
    private void hideAllCombatViewers() {
        for (int i=0; i<combatViewers.size(); i++) {
            combatPanels.get(i).setVisible(false);
        }
    }
    
    private void hideAllEncounterViewers() {
        for (int i=0; i<encounterViewers.size(); i++) {
            encounterPanels.get(i).setVisible(false);
        }
    }
    
    private void hideAllArtifactViewers() {
        for (int i=0; i<artifactPanels.size(); i++) {
            artifactPanels.get(i).setVisible(false);
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
    	if (p.x == 0 && p.y == 0) {
    		return;
    	}
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null) return;
        GameMetadata gm = g.getMetadata();
        if (p == null) {
            hideHexInfo();
            hidePopCenter();
            hideAllArmyViewers();
            hideAllArtifactViewers();
            hideAllCharacterViewers();
            hideAllCombatViewers();
            hideAllNationMessageViewers();
            return;
        }

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
        BeanComparator comp;
        
        //BeanComparator comp = new BeanComparator("name");
        //Collections.sort(chars, comp);
        Collections.sort(chars, new CharacterDeathAllegianceNameComparator());

        for (Character ch : (Collection<Character>)chars) {
            if (ch.getHostage() != null && ch.getHostage()) continue;
            showCharacter(ch);
        }

        hideAllArmyViewers();
        c = t.getContainer(TurnElementsEnum.Army);
        ArrayList armies = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        //comp = new BeanComparator("nationAllegiance");
        //Collections.sort(armies, comp);
        Collections.sort(armies, new ArmyAllegianceNameComparator());

        for (Army a : (Collection<Army>)armies) {
            showArmy(a);
        }

        hideAllNationMessageViewers();
        c = t.getContainer(TurnElementsEnum.NationMessage);
        Collection items = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
        for (NationMessage obj : (Collection<NationMessage>)items) {
            showNationMessage(obj);
        }
        
        hideAllArtifactViewers();
        c = t.getContainer(TurnElementsEnum.Artifact);
        Collection artifacts = c.findAllByProperties(new String[]{"hexNo"}, new Object[]{h.getHexNo()});
        for (Artifact obj : (Collection<Artifact>)artifacts) {
            showArtifact(obj);
        }
        
        hideAllCombatViewers();
        c = t.getContainer(TurnElementsEnum.Combat);
        Collection combats = c.findAllByProperties(new String[]{"hexNo"}, new Object[]{h.getHexNo()});
        for (Combat obj : (Collection<Combat>)combats) {
            showCombat(obj);
        }
        
        hideAllEncounterViewers();
        c = t.getContainer(TurnElementsEnum.Encounter);
        Collection encounters = new ArrayList();
        encounters.addAll(c.findAllByProperties(new String[]{"hexNo"}, new Object[]{h.getHexNo()}));
        c = t.getContainer(TurnElementsEnum.Challenge);
        encounters.addAll(c.findAllByProperties(new String[]{"hexNo"}, new Object[]{h.getHexNo()}));
        for (Encounter obj : (Collection<Encounter>)encounters) {
            showEncounter(obj);
        }
        
        scp.getVerticalScrollBar().setValue(0);
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                Point p = (Point)e.getObject();
                refresh(p);
            }
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
            	Point p = MapPanel.instance().getSelectedHex();
            	if (p != null) refresh(p);
            }
            if (e.getEventType().equals(LifecycleEventsEnum.RefreshHexItems.toString())) {
                Point p = (Point)e.getObject();
                refresh(p);
            }
            
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                Point p = MapPanel.instance().getSelectedHex();
                refresh(p);
            }
            if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
            	Order o = (Order)e.getData();
                for (int i=0; i<characterPanels.size(); i++) {
                    if (!characterPanels.get(i).isVisible()) continue;
                    Character c = (Character)characterViewers.get(i).getFormObject();
                    if (c == o.getCharacter()) {
                    	characterViewers.get(i).refreshOrders(c);
                    	break;
                    }
                }
            }
            if (e.getEventType().equals(LifecycleEventsEnum.SelectCharEvent.toString())) { 
                Character c = (Character)e.getData();
                selectCharacter(c);
            }
            if (e.getEventType().equals(LifecycleEventsEnum.NoteUpdated.toString())) { 
                Point p = MapPanel.instance().getSelectedHex();
                refresh(p);
            }
        }
    }
    
    protected void selectCharacter(Character c) {
        for (int i=0; i<characterViewers.size(); i++) {
            if (characterPanels.get(i).isVisible()) {
                Character ch = (Character)characterViewers.get(i).getFormObject();
                if (c == ch) {
                    GraphicUtils.showView("currentHexDataViewer");
                }
            }
        }
    }



    
    
}

