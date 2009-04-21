package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.Arrays;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.CombatUtils;
import org.joverseer.ui.domain.NationStatisticsWrapper;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.springframework.richclient.application.Application;

/**
 * List view for nation statistics
 * 
 * @author Marios Skounakis
 */
public class NationStatisticsListView extends BaseItemListView {

    public NationStatisticsListView() {
        super(NationStatisticsTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {48, 52, 72, 52, 42, 42, 42, 42, 42, 42, 42, 52, 52, 52, 52, 52, 52};
    }


    protected AbstractListViewFilter[][] getFilters() {
        ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
        filters.addAll(Arrays.asList(NationFilter.createNationFilters(true)));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        return new AbstractListViewFilter[][] {filters.toArray(new AbstractListViewFilter[] {})};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g))
            return;
        ArrayList items = new ArrayList();
        Turn t = g.getTurn();
        for (int i = 1; i < 26; i++) {
            //TODO move NationStatisticsWrapper creation to table model or other place
            Integer capitalHex = null;
            PopulationCenter capital = (PopulationCenter) t.getContainer(TurnElementsEnum.PopulationCenter)
                    .findFirstByProperties(new String[] {"nationNo", "capital"}, new Object[] {i, true});
            if (capital != null) {
                capitalHex = capital.getHexNo();
            }
            NationStatisticsWrapper nsw = new NationStatisticsWrapper();
            nsw.setNationNo(i);
            nsw.setCharacters(0);
            nsw.setCharactersInCapital(0);
            nsw.setCommanders(0);
            for (Character c : (ArrayList<Character>) t.getContainer(TurnElementsEnum.Character).findAllByProperty(
                    "nationNo", nsw.getNationNo())) {
                if (c.getDeathReason() == CharacterDeathReasonEnum.NotDead) {
                    nsw.setCharacters(nsw.getCharacters() + 1);
                    if (capitalHex != null && c.getHexNo() == capitalHex) {
                        nsw.setCharactersInCapital(nsw.getCharactersInCapital() + 1);
                    }
                    if (c.getCommand() > 0) {
                        nsw.setCommanders(nsw.getCommanders() + 1);
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
            for (PopulationCenter pc : (ArrayList<PopulationCenter>) t.getContainer(TurnElementsEnum.PopulationCenter)
                    .findAllByProperty("nationNo", nsw.getNationNo())) {
                nsw.setPopCenters(nsw.getPopCenters() + 1);
                if (pc.getSize() == PopulationCenterSizeEnum.city) {
                    nsw.setCities(nsw.getCities() + 1);
                    nsw.setTaxBase(nsw.getTaxBase() + 4);
                } else if (pc.getSize() == PopulationCenterSizeEnum.majorTown) {
                    nsw.setMajorTowns(nsw.getMajorTowns() + 1);
                    nsw.setTaxBase(nsw.getTaxBase() + 3);
                } else if (pc.getSize() == PopulationCenterSizeEnum.town) {
                    nsw.setTowns(nsw.getTowns() + 1);
                    nsw.setTaxBase(nsw.getTaxBase() + 2);
                } else if (pc.getSize() == PopulationCenterSizeEnum.village) {
                    nsw.setVillages(nsw.getVillages() + 1);
                    nsw.setTaxBase(nsw.getTaxBase() + 1);
                } else if (pc.getSize() == PopulationCenterSizeEnum.camp) {
                    nsw.setCamps(nsw.getCamps() + 1);
                }
            }

            nsw.setArmies(0);
            nsw.setArmyEHI(0);
            nsw.setTroopCount(0);
            nsw.setNavies(0);
            nsw.setWarships(0);
            nsw.setTransports(0);
            for (Army a : (ArrayList<Army>) t.getContainer(TurnElementsEnum.Army).findAllByProperty("nationNo",
                    nsw.getNationNo())) {
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
                nsw.setArmyEHI(nsw.getArmyEHI() + CombatUtils.getNakedHeavyInfantryEquivalent(a, null));
                nsw.setTroopCount(nsw.getTroopCount() + a.computeNumberOfMen());
            }

            items.add(nsw);
        }
        ArrayList filteredItems = new ArrayList();
        for (Object item : items) {
            if (getActiveFilter() == null || getActiveFilter().accept(item)) {
                filteredItems.add(item);
            }
        }
        tableModel.setRows(filteredItems);
    }


}
