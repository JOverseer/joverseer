package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.Arrays;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.NationMap;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.ui.domain.NationStatisticsWrapper;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;

/**
 * List view for nation statistics
 *
 * @author Marios Skounakis
 */
public class NationStatisticsListView extends BaseItemListView {

	public NationStatisticsListView() {
		super(NationStatisticsTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 32, 48, 52, 72, 72, 52, 40,40,42, 42, 42, 42, 42, 42, 42, 52, 52, 52, 52, 52, 52 };
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.addAll(Arrays.asList(NationFilter.createNationFilters(true)));
		filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters(true)));
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}) };
	}

	@Override
	protected void setItems() {
		Turn t = this.getTurn();
		if (t == null)
			return;
		ArrayList<NationStatisticsWrapper> items = new ArrayList<NationStatisticsWrapper>();
		NationStatisticsWrapper fp = new NationStatisticsWrapper();
		fp.setAllegiance(NationAllegianceEnum.FreePeople);

		NationStatisticsWrapper ds = new NationStatisticsWrapper();
		ds.setAllegiance(NationAllegianceEnum.DarkServants);

		NationStatisticsWrapper ns = new NationStatisticsWrapper();
		ns.setAllegiance(NationAllegianceEnum.Neutral);

		int limit = InfoUtils.getCharactersAllowed(this.game.getMetadata().getGameType(), this.game.getCurrentTurn());
		int npcRecruitLimit = InfoUtils.getNPCRecruitsAllowed(this.game.getMetadata().getGameType(), this.game.getCurrentTurn()); 
		for (int i = 0; i < 28; i++) {
			NationRelations nr = t.getNationRelations(i);
			if (nr != null && nr.getEliminated())
				continue;
			Nation n = NationMap.getNationFromNo(i);
			if (n == null) // NPC nations that aren't present.
				continue;
			if (n.getRemoved())
				continue;
			Integer capitalHex = null;
			PopulationCenter capital = (PopulationCenter) t.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] { "nationNo", "capital" }, new Object[] { i, true });
			if (capital != null) {
				capitalHex = capital.getHexNo();
			}
			NationStatisticsWrapper nsw = new NationStatisticsWrapper();
			nsw.setNationNo(i);
			nsw.setCharacters(0);
			nsw.setCharactersLimit(limit);
// TODO: earlier turns don't seem to get shown as different.
			nsw.setNPCsRecruited(n.getNPCsRecruited());
			nsw.setNPCRecruitLimit(npcRecruitLimit);
			nsw.setCharactersInCapital(0);
			nsw.setCommanders(0);
			for (Character c : t.getCharacters().findAllByProperty("nationNo", nsw.getNationNo())) {
				if (c.getDeathReason() == CharacterDeathReasonEnum.NotDead) {
					nsw.setCharacters(nsw.getCharacters() + 1);
					if (c.getHostage() != null && c.getHostage()) {
						nsw.setHostages(nsw.getHostages() + 1);
					} else {
						if (capitalHex != null && c.getHexNo() == capitalHex) {
							nsw.setCharactersInCapital(nsw.getCharactersInCapital() + 1);
						}
						if (c.getCommand() > 0) {
							nsw.setCommanders(nsw.getCommanders() + 1);
						}
					}
				}
			}


			nsw.setPopCenters(0);
			nsw.setCities(0);
			nsw.setMajorTowns(0);
			nsw.setTowns(0);
			nsw.setVillages(0);
			nsw.setCamps(0);
			nsw.setTaxBase(0);
			for (PopulationCenter pc : t.getPopulationCenters().findAllByProperty("nationNo", nsw.getNationNo())) {
				if (!pc.getNationNo().equals(nsw.getNationNo()))
					continue;
				nsw.incPopCentre(pc.getSize());
			}

			nsw.setArmies(0);
			nsw.setTroopCount(0);
			nsw.setNavies(0);
			nsw.setWarships(0);
			nsw.setTransports(0);
			for (Army a : t.getArmies().findAllByProperty("nationNo", nsw.getNationNo())) {
				if (!a.isNavy()) {
					nsw.setArmies(nsw.getArmies() + 1);
				} else {
					nsw.setNavies(nsw.getNavies() + 1);
					ArmyElement ae = a.getElement(ArmyElementType.Warships);
					if (ae != null) {
						nsw.setWarships(nsw.getWarships() + ae.getNumber());
					}
					ae = a.getElement(ArmyElementType.Transports);
					if (ae != null) {
						nsw.setTransports(nsw.getTransports() + ae.getNumber());
					}
				}
				nsw.setTroopCount(nsw.getTroopCount() + a.computeNumberOfMen());
			}
			if (nr == null) {
				// nothing
			} else if (nr.getAllegiance().equals(NationAllegianceEnum.FreePeople)) {
				fp.add(nsw);
			} else if (nr.getAllegiance().equals(NationAllegianceEnum.DarkServants)) {
				ds.add(nsw);
			} else {
				ns.add(nsw);
			}
			items.add(nsw);
		}
		boolean addFP = false;
		boolean addDS = false;
		boolean addNeutrals = false;
		ArrayList<NationStatisticsWrapper> filteredItems = new ArrayList<NationStatisticsWrapper>();
		for (NationStatisticsWrapper item : items) {
			if (getActiveFilter() == null || getActiveFilter().accept(item)) {
				filteredItems.add(item);
				NationRelations nr = t.getNationRelations(item.getNationNo());
				if (nr == null)
					continue;
				if (nr.getAllegiance().equals(NationAllegianceEnum.FreePeople))
					addFP = true;
				if (nr.getAllegiance().equals(NationAllegianceEnum.DarkServants))
					addDS = true;
				if (nr.getAllegiance().equals(NationAllegianceEnum.Neutral))
					addNeutrals = true;
			}
		}
		if (addFP)
			filteredItems.add(fp);
		if (addDS)
			filteredItems.add(ds);
		if (addNeutrals)
			filteredItems.add(ns);

		this.tableModel.setRows(filteredItems);
	}
	/**
	 * Get a handle to the underlying model, so that another view can hook in.
	 * @return
	 */
	public NationStatisticsTableModel getTableModel() {
		if (this.table == null) return null;
		return (NationStatisticsTableModel)(this.tableModel);
	}

}
