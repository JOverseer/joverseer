package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

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
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ArmyAllegianceNameComparator;
import org.joverseer.tools.CharacterDeathAllegianceNameComparator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.support.DefaultViewDescriptor;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Shows information for the current hex, using the various viewers in this
 * package
 *
 * For some object types (e.g. pop center or hex info) only one viewer is
 * created For other object types (e.g. characters), multiple viewers are
 * created
 *
 * Viewers are shown/hidden as needed
 *
 * Created by spring from richclient-page-application-context.xml
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
	Color background;

	//injected dependencies
	GameHolder gameHolder;
	public GameHolder getGameHolder() {
		return this.gameHolder;
	}
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	@Override
	protected JComponent createControl() {
		this.background = UIManager.getColor("Panel.background");
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		this.popCenterViewer = new PopulationCenterViewer(FormModelHelper.createFormModel(new PopulationCenter()),this.gameHolder);
		this.popCenterPanel = new JPanel();
		this.popCenterPanel.add(this.popCenterViewer.getControl());
		tlb.separator(Messages.getString("CurrentHexDataViewer.PopulationCenter")); //$NON-NLS-1$
		tlb.row();
		tlb.cell(this.popCenterPanel, "align=left"); //$NON-NLS-1$
		tlb.row();
		this.popCenterPanel.setVisible(false);
		this.popCenterPanel.setBackground(this.background);

		tlb.separator(Messages.getString("CurrentHexDataViewer.Armies")); //$NON-NLS-1$
		tlb.row();
		for (int i = 0; i < 20; i++) {
			ArmyViewer va = new ArmyViewer(FormModelHelper.createFormModel(new Army()),this.gameHolder);
			this.armyViewers.add(va);
			JPanel cp = new JPanel();
			cp.add(va.getControl());
			cp.setBackground(this.background);
			this.armyPanels.add(cp);
			tlb.cell(cp, "align=left"); //$NON-NLS-1$
			tlb.row();
			cp.setVisible(false);
		}

		tlb.separator(Messages.getString("CurrentHexDataViewer.Characters")); //$NON-NLS-1$
		tlb.row();
		for (int i = 0; i < 50; i++) {
			CharacterViewer vc = new CharacterViewer(FormModelHelper.createFormModel(new Character()),this.gameHolder);
			this.characterViewers.add(vc);
			JPanel cp = new JPanel();
			cp.add(vc.getControl());
			cp.setBackground(this.background);
			this.characterPanels.add(cp);
			tlb.cell(cp, "align=left"); //$NON-NLS-1$
			tlb.row();
			cp.setVisible(false);
		}

		tlb.separator(Messages.getString("CurrentHexDataViewer.Artifacts")); //$NON-NLS-1$
		tlb.row();
		for (int i = 0; i < 20; i++) {
			ArtifactViewer av = new ArtifactViewer(FormModelHelper.createFormModel(new Artifact()),this.gameHolder);
			JPanel ap = new JPanel();
			this.artifactViewers.add(av);
			this.artifactPanels.add(ap);
			ap.add(av.getControl());
			ap.setBackground(this.background);
			tlb.cell(ap, "align=left"); //$NON-NLS-1$
			tlb.row();
			ap.setVisible(false);
		}

		tlb.separator(Messages.getString("CurrentHexDataViewer.NationMessages")); //$NON-NLS-1$
		tlb.row();
		for (int i = 0; i < 20; i++) {
			NationMessageViewer nmv = new NationMessageViewer(FormModelHelper.createFormModel(new NationMessage()),this.gameHolder);
			this.nationMessageViewers.add(nmv);
			JPanel cp = new JPanel();
			cp.add(nmv.getControl());
			cp.setBackground(this.background);
			this.nationMessagePanels.add(cp);
			tlb.cell(cp, "align=left"); //$NON-NLS-1$
			tlb.row();
			cp.setVisible(false);
		}

		tlb.separator(Messages.getString("CurrentHexDataViewer.CombatsChallengesEncounters")); //$NON-NLS-1$
		tlb.row();
		for (int i = 0; i < 5; i++) {
			CombatViewer cv = new CombatViewer(FormModelHelper.createFormModel(new Combat()),this.gameHolder);
			this.combatViewers.add(cv);
			JPanel cp = new JPanel();
			cp.add(cv.getControl());
			cp.setBackground(this.background);
			this.combatPanels.add(cp);
			tlb.cell(cp, "align=left"); //$NON-NLS-1$
			tlb.row();
			cp.setVisible(false);
		}
		for (int i = 0; i < 5; i++) {
			EncounterViewer cv = new EncounterViewer(FormModelHelper.createFormModel(new Encounter()),this.gameHolder);
			this.encounterViewers.add(cv);
			JPanel cp = new JPanel();
			cp.add(cv.getControl());
			cp.setBackground(this.background);
			this.encounterPanels.add(cp);
			tlb.cell(cp, "align=left"); //$NON-NLS-1$
			tlb.row();
			cp.setVisible(false);
		}

		this.hexInfoViewer = new HexInfoViewer(FormModelHelper.createFormModel(new Hex()),this.gameHolder);
		this.hexInfoPanel = new JPanel();
		this.hexInfoPanel.add(this.hexInfoViewer.getControl());
		tlb.separator(Messages.getString("CurrentHexDataViewer.HexInfo")); //$NON-NLS-1$
		tlb.row();
		tlb.cell(this.hexInfoPanel, "align=left"); //$NON-NLS-1$
		tlb.row();
		this.hexInfoPanel.setVisible(false);
		this.hexInfoPanel.setBackground(this.background);

		this.panel = tlb.getPanel();
		this.panel.setBackground(this.background);
		this.panel.setPreferredSize(new Dimension(240, 3000));
		this.scp = new JScrollPane(this.panel);
		this.scp.setPreferredSize(new Dimension(240, 1500));
		this.scp.getVerticalScrollBar().setUnitIncrement(32);
		return this.scp;
	}

	private void showPopCenter(PopulationCenter pc) {
		this.popCenterViewer.setFormObject(pc);
		this.popCenterPanel.setVisible(true);
	}

	private void hidePopCenter() {
		this.popCenterPanel.setVisible(false);
	}

	private void showHexInfo(Hex h) {
		this.hexInfoViewer.setFormObject(h);
		this.hexInfoPanel.setVisible(true);

	}

	private void hideHexInfo() {
		this.hexInfoPanel.setVisible(false);
	}

	private void showCharacter(Character c) {
		for (int i = 0; i < this.characterViewers.size(); i++) {
			if (!this.characterPanels.get(i).isVisible()) {
				this.characterViewers.get(i).setFormObject(c);
				this.characterPanels.get(i).setVisible(true);
				return;
			}
		}
	}

	private void showArmy(Army a) {
		for (int i = 0; i < this.armyViewers.size(); i++) {
			if (!this.armyPanels.get(i).isVisible()) {
				this.armyViewers.get(i).setFormObject(a);
				this.armyPanels.get(i).setVisible(true);
				return;
			}
		}
	}

	private void showNationMessage(NationMessage c) {
		for (int i = 0; i < this.nationMessageViewers.size(); i++) {
			if (!this.nationMessagePanels.get(i).isVisible()) {
				this.nationMessageViewers.get(i).setFormObject(c);
				this.nationMessagePanels.get(i).setVisible(true);
				return;
			}
		}
	}

	private void showArtifact(Artifact a) {
		for (int i = 0; i < this.artifactPanels.size(); i++) {
			if (!this.artifactPanels.get(i).isVisible()) {
				this.artifactViewers.get(i).setFormObject(a);
				this.artifactPanels.get(i).setVisible(true);
				return;
			}
		}
	}

	public void showCombat(Combat c) {
		for (int i = 0; i < this.combatPanels.size(); i++) {
			if (!this.combatPanels.get(i).isVisible()) {
				this.combatViewers.get(i).setFormObject(c);
				this.combatPanels.get(i).setVisible(true);
				return;
			}
		}
	}

	public void showEncounter(Encounter c) {
		for (int i = 0; i < this.encounterPanels.size(); i++) {
			if (!this.encounterPanels.get(i).isVisible()) {
				this.encounterViewers.get(i).setFormObject(c);
				this.encounterPanels.get(i).setVisible(true);
				return;
			}
		}
	}

	private void hideAllCharacterViewers() {
		for (int i = 0; i < this.characterViewers.size(); i++) {
			this.characterPanels.get(i).setVisible(false);
		}

	}

	private void hideAllCombatViewers() {
		for (int i = 0; i < this.combatViewers.size(); i++) {
			this.combatPanels.get(i).setVisible(false);
		}
	}

	private void hideAllEncounterViewers() {
		for (int i = 0; i < this.encounterViewers.size(); i++) {
			this.encounterPanels.get(i).setVisible(false);
		}
	}

	private void hideAllArtifactViewers() {
		for (int i = 0; i < this.artifactPanels.size(); i++) {
			this.artifactPanels.get(i).setVisible(false);
		}
	}

	private void hideAllNationMessageViewers() {
		for (int i = 0; i < this.nationMessageViewers.size(); i++) {
			this.nationMessagePanels.get(i).setVisible(false);
		}

	}

	private void hideAllArmyViewers() {
		for (int i = 0; i < this.armyPanels.size(); i++) {
			this.armyPanels.get(i).setVisible(false);
		}

	}

	private void refresh(Point p) {
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
		String hex = ""; //$NON-NLS-1$
		if ((p.x == 0 && p.y == 0) || p.x == -1) {
			((DefaultViewDescriptor) getDescriptor()).setTitle(Messages.getString("CurrentHexDataViewer.Title") + hex); //$NON-NLS-1$
			return;
		}
		hex = String.valueOf(p.x * 100 + p.y);
		if (p.x < 10)
			hex = "0" + hex; //$NON-NLS-1$
		((DefaultViewDescriptor) getDescriptor()).setTitle(Messages.getString("CurrentHexDataViewer.Title") + hex); //$NON-NLS-1$
		Game g = this.gameHolder.getGame(); //$NON-NLS-1$
		if (g == null)
			return;
		GameMetadata gm = g.getMetadata();

		int hexno = p.x * 100 + p.y;
		Hex h = gm.getHex(hexno);
		if (h != null) {
			showHexInfo(h);
			hexno = h.getHexNo();
		} else {
			hideHexInfo();
		}
		Turn t = g.getTurn();
		PopulationCenter pc = t.getPopulationCenters().findFirstByProperties(new String[] { "x", "y" }, new Object[] { p.x, p.y }); //$NON-NLS-1$ //$NON-NLS-2$
		if (pc != null) {
			showPopCenter(pc);
		} else {
			hidePopCenter();
		}

		hideAllCharacterViewers();
		ArrayList<Character> chars = t.getCharacters().findAllByProperties(new String[] { "x", "y" }, new Object[] { p.x, p.y }); //$NON-NLS-1$ //$NON-NLS-2$
		Collections.sort(chars, new CharacterDeathAllegianceNameComparator());

		for (Character ch : chars) {
			if (ch.getHostage() != null && ch.getHostage().booleanValue())
				continue;
			showCharacter(ch);
		}

		hideAllArmyViewers();
		ArrayList<Army> armies = t.getArmies().findAllByProperties(new String[] { "x", "y" }, new Object[] { p.x, p.y }); //$NON-NLS-1$ //$NON-NLS-2$
		Collections.sort(armies, new ArmyAllegianceNameComparator());

		for (Army a : armies) {
			showArmy(a);
		}

		hideAllNationMessageViewers();
		Collection<NationMessage> items = t.getNationMessages().findAllByProperties(new String[] { "x", "y" }, new Object[] { p.x, p.y }); //$NON-NLS-1$ //$NON-NLS-2$
		for (NationMessage obj : items) {
			showNationMessage(obj);
		}

		hideAllArtifactViewers();
		Collection<Artifact> artifacts = t.getArtifacts().findAllByProperties(new String[] { "hexNo" }, new Object[] { hexno }); //$NON-NLS-1$
		for (Artifact obj : artifacts) {
			showArtifact(obj);
		}

		hideAllCombatViewers();
		Collection<Combat> combats = t.getCombats().findAllByProperties(new String[] { "hexNo" }, new Object[] { hexno }); //$NON-NLS-1$
		for (Combat obj : combats) {
			showCombat(obj);
		}

		hideAllEncounterViewers();
		Collection<Encounter> encounters = new ArrayList<Encounter>();
		encounters.addAll(t.getEncounters().findAllByProperties(new String[] { "hexNo" }, new Object[] { hexno })); //$NON-NLS-1$
		encounters.addAll(t.getChallenges().findAllByProperties(new String[] { "hexNo" }, new Object[] { hexno })); //$NON-NLS-1$
		for (Encounter obj : encounters) {
			showEncounter(obj);
		}

		this.scp.getVerticalScrollBar().setValue(0);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.isLifecycleEvent(LifecycleEventsEnum.SelectedHexChangedEvent)) {
				Point p = (Point) e.getObject();
				refresh(p);
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.GameChangedEvent)) {
				Point p = MapPanel.instance().getSelectedHex();
				if (p != null)
					refresh(p);
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.RefreshHexItems)) {
				Point p = (Point) e.getObject();
				refresh(p);
			}

			if (e.isLifecycleEvent(LifecycleEventsEnum.SelectedTurnChangedEvent)) {
				Point p = MapPanel.instance().getSelectedHex();
				refresh(p);
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.OrderChangedEvent)) {
				Order o = (Order) e.getData();
				for (int i = 0; i < this.characterPanels.size(); i++) {
					if (!this.characterPanels.get(i).isVisible())
						continue;
					Character c = (Character) this.characterViewers.get(i).getFormObject();
					if (c == o.getCharacter()) {
						this.characterViewers.get(i).refreshOrders(c);
						break;
					}
				}
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.SelectCharEvent)) {
				Character c = (Character) e.getData();
				selectCharacter(c);
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.NoteUpdated)) {
				Point p = MapPanel.instance().getSelectedHex();
				refresh(p);
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.OrderCheckerRunEvent)) {
				for (int i = 0; i < this.characterPanels.size(); i++) {
					if (!this.characterPanels.get(i).isVisible())
						continue;
					Character c = (Character) this.characterViewers.get(i).getFormObject();
						this.characterViewers.get(i).refreshOrders(c);
				}
								
			}
			if(e.isLifecycleEvent(LifecycleEventsEnum.ThemeChangeEvent)) {
				removeBordersFromTextFields(this.getControl());
			}
		}
	}
	
    public static void removeBordersFromTextFields(JComponent component) {
        for (Component comp : component.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setBorder(null);
                ((JTextField) comp).setOpaque(false);
            } else if (comp instanceof JComponent) {
                removeBordersFromTextFields((JComponent) comp); // recursive call
            }
        }
    }

	protected void selectCharacter(Character c) {
		for (int i = 0; i < this.characterViewers.size(); i++) {
			if (this.characterPanels.get(i).isVisible()) {
				Character ch = (Character) this.characterViewers.get(i).getFormObject();
				if (c == ch) {
					GraphicUtils.showView("currentHexDataViewer"); //$NON-NLS-1$
				}
			}
		}
	}

}
