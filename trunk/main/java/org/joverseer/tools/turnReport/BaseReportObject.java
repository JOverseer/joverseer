package org.joverseer.tools.turnReport;

import java.awt.Point;
import java.util.ArrayList;

import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.NationMap;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.commands.DialogsUtility;
import org.springframework.richclient.application.Application;

public class BaseReportObject implements IHasMapLocation, Comparable<Object> {
	String name;
	ObjectModificationType modification;
	int hexNo;
	int nationNo;
	String notes;
	ArrayList<Integer> nations = new ArrayList<Integer>();

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNationNo() {
		return nationNo;
	}

	public void setNationNo(Integer nationNo) {
		if (new Integer(this.nationNo).equals(nationNo))
			return;
		removeNation(nationNo);
		this.nationNo = nationNo;
		addNation(nationNo);
	}

	public boolean containsNation(int nationNo) {
		return nations.contains(nationNo);
	}

	public ObjectModificationType getModification() {
		return modification;
	}

	public void setModification(ObjectModificationType modification) {
		this.modification = modification;
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getNotes() {
		return notes == null ? "" : notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public ArrayList<Integer> getNations() {
		return nations;
	}

	public void setNations(ArrayList<Integer> nations) {
		this.nations = nations;
	}

	public int getX() {
		return getHexNo() / 100;
	}

	public int getY() {
		return getHexNo() % 100;
	}

	public void removeNation(int nationNo) {
		if (this.nationNo == nationNo)
			this.nationNo = 0;
		if (nations.contains(nationNo))
			nations.remove(nationNo);
	}

	public void addNation(int nationNo) {
		if (nationNo == 0)
			return;
		if (!nations.contains(nationNo))
			nations.add(nationNo);
		if (this.nationNo == 0)
			this.nationNo = nationNo;
	}

	public void appendNote(String note) {
		String n = getNotes();
		if (n == null)
			n = "";
		n += (n.equals("") ? "" : ",") + note;
		setNotes(n);
	}

	public void appendNotePart(String notePart) {
		String n = getNotes();
		if (n == null)
			n = "";
		n += notePart;
		setNotes(n);
	}

	@Override
	public String toString() {
		return getHexNo() + " - " + getNationStr() + " - " + getName() + " - " + getModification() + " - " + getNotes();
	}

	public String getNationsStr() {
		String ret = "";
		for (int nationNo : getNations()) {
			if (nationNo == 0)
				continue;
			Nation n = NationMap.getNationFromNo(nationNo);
			ret += (ret.equals("") ? "" : ",") + n.getShortName();
		}
		return ret;
	}

	public String getNationStr() {
		if (getNationNo() == 0)
			return "";
		Nation n = NationMap.getNationFromNo(getNationNo());
		return n.getShortName();
	}

	public int compareTo(Object o) {
		if (BaseReportObject.class.isInstance(o)) {
			BaseReportObject ro = (BaseReportObject) o;
			if (getNationNo() == 0)
				return 1;
			if (ro.getNationNo() == 0)
				return -1;
			int i = getNationNo().compareTo(ro.getNationNo());
			if (i != 0)
				return i;
			return getModification().compareTo(ro.getModification());
		}
		return 0;
	}

	public String getHtmlString() {
		return
		// appendTd(getNationStr()) +
		// appendTd(getModification()) +
		appendTd(getNationsStr()) + appendTd(getName()) + appendTd(getHexNoStr()) + appendTd(getNotes()) + appendTd(getExtraInfo()) + appendTd(getLinks());

	}

	public String getHexNoStr() {
		if (getHexNo() == 0)
			return "";
		String c = String.valueOf(getHexNo());
		if (getHexNo() < 1000)
			c = "0" + c;
		return c;
	}

	public String getLinks() {
		return "<a href='http://event?hex=" + getHexNo() + "'>Hex</a>";
	}

	protected String appendTd(Object str) {
		return "<td style='border-style:solid; border-width:1px;'>" + (str == null ? "" : str.toString()) + "</td>";
	}

	public String getExtraInfo() {
		return "";
	}

	public static void processHyperlink(String query) {
		query = query.substring(query.indexOf("?") + 1);
		String[] qs = query.split("&");
		for (String q : qs) {
			String[] ps = q.split("=");
			if (ps[0].equals("hex")) {
				int hexNo = Integer.parseInt(ps[1]);
				if (hexNo != 0) {
					Point p = new Point(hexNo / 100, hexNo % 100);
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), p, null));
				}
			} else if (ps[0].equals("report")) {
				String charId = ps[1].replace("_", " ");
				Character c = GameHolder.instance().getGame().getTurn().getCharById(charId);
				if (c != null) {
					DialogsUtility.showCharacterOrderResults(c);
				}
			} else if (ps[0].equals("combat")) {
				int hexNo = Integer.parseInt(ps[1]);
				Combat c = (Combat) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Combat).findFirstByProperty("hexNo", hexNo);
				if (c != null)
					DialogsUtility.showCombatNarration(c);

			} else if (ps[0].equals("enc")) {
				String[] params = ps[1].split(",");
				int hexNo = Integer.parseInt(params[0]);
				String charId = params[1].replace("_", " ");
				Turn t = GameHolder.instance().getGame().getTurn();

				Character c = t.getCharById(charId);
				Encounter encounter = t.getEncounter(hexNo, c.getName());
				DialogsUtility.showEncounterDescription(encounter);
			} else if (ps[0].equals("challenge")) {
				String[] params = ps[1].split(",");
				Turn t = GameHolder.instance().getGame().getTurn();
				String charId = params[0].replace("_", " ");
				Challenge challenge = null;
				Character c = t.getCharById(charId);
				if (c != null)
					challenge = t.findChallenge(c.getName());
				if (challenge == null) {
					charId = params[1].replace("_", " ");
					c = t.getCharById(charId);
					if (c == null)
						return;
					challenge = t.findChallenge(c.getName());
				}
				if (challenge == null)
					return;
				DialogsUtility.showChallengeDescription(challenge);
			}
		}
	}
}
