package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;

/**
 * Filter for Nations
 * 
 * Items must implements the IBelongsToNation interface
 * 
 * @author Marios Skounakis
 */
public class NationFilter extends AbstractListViewFilter {
	static int ALL_NATIONS = -1;
	static int ALL_IMPORTED = -2;
	static int ADDITIONAL_NATIONS = -3;

	int nationNo;

	public NationFilter(String description, int nationNo) {
		super(description);
		this.nationNo = nationNo;
	}

	@Override
	public boolean accept(Object obj) {
		IBelongsToNation o = (IBelongsToNation) obj;
		if (nationNo == ALL_NATIONS)
			return true;
		if (nationNo == ALL_IMPORTED) {
			GameHolder.instance();
			if (!GameHolder.hasInitializedGame())
				return true;
			return GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", o.getNationNo()) != null;
		}
		if (nationNo == ADDITIONAL_NATIONS) {
			GameHolder.instance();
			if (!GameHolder.hasInitializedGame())
				return true;
			Game g = GameHolder.instance().getGame();
			if (o.getNationNo() == null)
				return true;
			int nationNo = g.getMetadata().getNationNo();
			if (nationNo == o.getNationNo())
				return true;
			String additionalNations = g.getMetadata().getAdditionalNations();
			if (additionalNations != null) {
				try {
					String[] ps = additionalNations.split(",");
					for (String p : ps) {
						if (Integer.parseInt(p) == o.getNationNo())
							return true;
					}
				} catch (Exception e) {
					// do nothing
				}
			}
			return false;
		}
		return o.getNationNo() != null && o.getNationNo() == nationNo;
	}

	public static AbstractListViewFilter[] createNationFilters() {
		return createNationFilters(false);
	}

	/**
	 * Creates the standard nation filters: - All nations - All imported (if
	 * includeAllImported == true) - One for each nation in the game metadata
	 */
	public static AbstractListViewFilter[] createNationFilters(boolean includeAllImported) {
		ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
		// ret.add(new NationFilter("Mine", ADDITIONAL_NATIONS));
		if (includeAllImported) {
			ret.add(new NationFilter("All imported", ALL_IMPORTED));
		}
		ret.add(new NationFilter("All", ALL_NATIONS));
		Game g = GameHolder.instance().getGame();
		if (!Game.isInitialized(g))
			return ret.toArray(new AbstractListViewFilter[] {});

		String pref = PreferenceRegistry.instance().getPreferenceValue("listviews.nationFilterOrder");
		if (pref == null || pref.equals("nationNumber")) {
			for (Nation n : g.getMetadata().getNations()) {
				ret.add(new NationFilter(n.getName(), n.getNumber()));
			}
		} else {
			// find the allegiance of the game's nation
			int nationNo = g.getMetadata().getNationNo();
			Nation mn = g.getMetadata().getNationByNum(nationNo);
			if (mn.getAllegiance() == NationAllegianceEnum.DarkServants) {
				for (Nation n : g.getMetadata().getNations()) {
					if (n.getAllegiance() == mn.getAllegiance()) {
						ret.add(new NationFilter(n.getName(), n.getNumber()));
					}
				}
				for (Nation n : g.getMetadata().getNations()) {
					if (n.getAllegiance() != mn.getAllegiance()) {
						ret.add(new NationFilter(n.getName(), n.getNumber()));
					}
				}
			} else {
				for (Nation n : g.getMetadata().getNations()) {
					ret.add(new NationFilter(n.getName(), n.getNumber()));
				}
			}
		}
		return ret.toArray(new AbstractListViewFilter[] {});
	}

	/**
	 * Creates a list of nation filters with: - All imported - All No specific
	 * nations are included
	 */
	public static AbstractListViewFilter[] createAllAndAllImportedNationFilters() {
		ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
		ret.add(new NationFilter("All imported", ALL_IMPORTED));
		ret.add(new NationFilter("All", ALL_NATIONS));
		return ret.toArray(new AbstractListViewFilter[] {});
	}
}
