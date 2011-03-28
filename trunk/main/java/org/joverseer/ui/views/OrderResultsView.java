package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class OrderResultsView extends BaseHtmlReportView {
	JTextField charName;
	JTextField text;
	JTextField turnFrom;
	JTextField turnTo;
	JTextField nationNo;

	@Override
	protected JPanel getCriteriaPanel() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("Character"));
		tlb.gapCol();
		tlb.cell(charName = new JTextField(), "colspec=left:100");
		charName.setPreferredSize(new Dimension(100, 20));
		tlb.relatedGapRow();
		tlb.cell(new JLabel("Text"));
		tlb.gapCol();
		tlb.cell(text = new JTextField(), "colspec=left:100");
		text.setPreferredSize(new Dimension(100, 20));
		tlb.relatedGapRow();
		tlb.cell(new JLabel("From turn"));
		tlb.gapCol();
		tlb.cell(turnFrom = new JTextField(), "colspec=left:100");
		turnFrom.setPreferredSize(new Dimension(100, 20));
		tlb.relatedGapRow();
		tlb.cell(new JLabel("To turn"));
		tlb.gapCol();
		tlb.cell(turnTo = new JTextField(), "colspec=left:100");
		turnTo.setPreferredSize(new Dimension(100, 20));
		tlb.relatedGapRow();
		tlb.cell(new JLabel("Nation"));
		tlb.gapCol();
		tlb.cell(nationNo = new JTextField(), "colspec=left:100");
		nationNo.setPreferredSize(new Dimension(100, 20));
		tlb.relatedGapRow();
		return tlb.getPanel();
	}

	@Override
	protected void handleHyperlinkEvent(String url) {
		String query = url;
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
			} else if (ps[0].equals("turn")) {
				int turnNo = Integer.parseInt(ps[1]);
				GameHolder.instance().getGame().setCurrentTurn(turnNo);
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, null));
			}
		}
	}

	@Override
	protected String getReportContents() {
		if (!GameHolder.hasInitializedGame())
			return "";
		String ret = "<html><body><div style='font-family:Tahoma; font-size:11pt'><table><tr><td width=700>";

		String charFilter = charName.getText();
		String textFilter = text.getText();
		String turnFromFilter = turnFrom.getText();
		String turnToFilter = turnTo.getText();
		String nationFilter = nationNo.getText();

		int turnFromValue = -1;
		try {
			turnFromValue = Integer.parseInt(turnFromFilter);
		} catch (Exception e) {
			// nothing
		}
		int turnToValue = 1000;
		try {
			turnToValue = Integer.parseInt(turnToFilter);
		} catch (Exception e) {
			// nothing
		}
		int nationValue = -1;
		try {
			nationValue = Integer.parseInt(nationFilter);
		} catch (Exception e) {
			// nothing
		}

		Game game = GameHolder.instance().getGame();
		for (int i = 0; i <= game.getCurrentTurn(); i++) {
			if (i < turnFromValue || i > turnToValue)
				continue;
			Turn t = game.getTurn(i);
			if (t == null)
				continue;
			ret += "<b><u>Turn " + i + "</u></b><p/>";
			for (Character c : t.getCharacters()) {
				if (nationValue > -1 && !c.getNationNo().equals(nationValue))
					continue;
				if (!c.getName().contains(charFilter))
					continue;
				String cor = c.getCleanOrderResults();
				if (cor == null || cor.equals("") || !cor.contains(textFilter))
					continue;
				cor = cor.replace("/", "-");
				if (textFilter.length() > 0) {
					cor = cor.replace(textFilter, "<font bgcolor=yellow>" + textFilter + "</font>");
				}
				ret += "<b>" + c.getName() + "&nbsp;&nbsp;(" + c.getNation().getShortName() + ",&nbsp;" + c.getStatString() + ") @ <a href='http://www.event.test?turn=" + i + "&hex=" + c.getHexNo() + "'>" + c.getHexNo() + "</a></b><br/>" + cor + "<p/><p/>";
			}
		}
		return ret;
	}

}
