package org.joverseer.tools;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;

public class GameEncounterReportCollector {

	public String CollectEncounters() {
		if (!GameHolder.hasInitializedGame())
			return "";
		Game g = GameHolder.instance().getGame();
		String ret = "<html><div style='font-family:Tahoma; font-size:10pt'>";
		for (int i = 0; i <= g.getCurrentTurn(); i++) {
			ArrayList<EncounterPair> pairs = new ArrayList<EncounterPair>();
			Turn pt = g.getTurn(i);
			if (pt == null)
				continue;
			Turn nt = g.getTurn(i + 1);
			ArrayList<Encounter> pes = pt.getEncounters().getItems();
			for (Encounter pe : pes) {
				if (pe.isReacting()) {
					Encounter ne = null;
					if (nt != null) {
						ne = nt.getReactionEncounter(pe.getHexNo());
					}
					EncounterPair ep = new EncounterPair(pe, ne);
					if (ne != null) {
						ep.setReactionCharacter(pt.getCharByName(ne.getCharacter()));
					}
					pairs.add(ep);
				}
			}
			if (pairs.size() > 0) {
				ret += "<hr/>";
				ret += "<b>Turn " + i + "</b><br/>";
				ret += "<table>";
				for (EncounterPair ep : pairs) {
					ret += "<tr><td width=400 valign=top>";
					ret += "<b><i>Encounter for " + ep.getReacting().getCharacter() + " at " + ep.getReacting().getHexNo() + "</i></b><br/>";
					ret += ep.getReacting().getDescription() + "</td><td width=400 valign=top>";
					if (ep.getReaction() != null) {
						ret += "<b><i>Encounter for " + ep.getReaction().getCharacter() + " at " + ep.getReaction().getHexNo() + "</i></b><br/>";
						if (ep.getReactionCharacter() != null) {
							String reaction = getReaction(ep.getReactionCharacter());
							ret += "<b>Reaction: " + (reaction == null ? "?" : reaction) + "</b><br/>";
						}
						ret += ep.getReaction().getDescription();
					} else {
						ret += "&nbsp;";
					}
					ret += "</td></tr>";
				}
				ret += "</table>";
			}

		}
		return ret;
	}

	protected Order findReactionOrder(Character c) {
		for (Order o : c.getOrders()) {
			if (o.getOrderNo() == 285)
				return o;
		}
		return null;
	}

	protected String getReaction(Character c) {
		Order o = findReactionOrder(c);
		if (o != null)
			return o.getP0();
		return null;
	}

	class EncounterPair {
		Encounter reacting;
		Encounter reaction;
		Character reactionCharacter;

		public EncounterPair() {
			super();
		}

		public EncounterPair(Encounter reacting, Encounter reacted) {
			super();
			this.reacting = reacting;
			this.reaction = reacted;
		}

		public Encounter getReacting() {
			return reacting;
		}

		public void setReacting(Encounter reacting) {
			this.reacting = reacting;
		}

		public Encounter getReaction() {
			return reaction;
		}

		public void setReaction(Encounter reacted) {
			this.reaction = reacted;
		}

		public Character getReactionCharacter() {
			return reactionCharacter;
		}

		public void setReactionCharacter(Character reactionCharacter) {
			this.reactionCharacter = reactionCharacter;
		}

	}

	class EncounterHolder {
		ArrayList<EncounterPair> pairs = new ArrayList<EncounterPair>();

		public void add(EncounterPair pair) {
			pairs.add(pair);
		}
	}

}
