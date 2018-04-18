package org.joverseer.ui.views;

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
import org.joverseer.ui.support.Messages;
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
		tlb.cell(new JLabel(Messages.getString("OrderResultsView.Character"))); //$NON-NLS-1$
		tlb.gapCol();
		tlb.cell(this.charName = new JTextField(), "colspec=left:100"); //$NON-NLS-1$
		this.charName.setPreferredSize(this.uiSizes.newTextPreferredDimension());
		tlb.relatedGapRow();
		tlb.cell(new JLabel(Messages.getString("OrderResultsView.Text"))); //$NON-NLS-1$
		tlb.gapCol();
		tlb.cell(this.text = new JTextField(), "colspec=left:100"); //$NON-NLS-1$
		this.text.setPreferredSize(this.uiSizes.newTextPreferredDimension());
		tlb.relatedGapRow();
		tlb.cell(new JLabel(Messages.getString("OrderResultsView.FromTurn"))); //$NON-NLS-1$
		tlb.gapCol();
		tlb.cell(this.turnFrom = new JTextField(), "colspec=left:100"); //$NON-NLS-1$
		this.turnFrom.setPreferredSize(this.uiSizes.newTextPreferredDimension());
		tlb.relatedGapRow();
		tlb.cell(new JLabel(Messages.getString("OrderResultsView.ToTurn"))); //$NON-NLS-1$
		tlb.gapCol();
		tlb.cell(this.turnTo = new JTextField(), "colspec=left:100"); //$NON-NLS-1$
		this.turnTo.setPreferredSize(this.uiSizes.newTextPreferredDimension());
		tlb.relatedGapRow();
		tlb.cell(new JLabel(Messages.getString("OrderResultsView.Nation"))); //$NON-NLS-1$
		tlb.gapCol();
		tlb.cell(this.nationNo = new JTextField(), "colspec=left:100"); //$NON-NLS-1$
		this.nationNo.setPreferredSize(this.uiSizes.newTextPreferredDimension());
		tlb.relatedGapRow();
		return tlb.getPanel();
	}

	@Override
	protected void handleHyperlinkEvent(String url) {
		String query = url;
		query = query.substring(query.indexOf("?") + 1); //$NON-NLS-1$
		String[] qs = query.split("&"); //$NON-NLS-1$
		for (String q : qs) {
			String[] ps = q.split("="); //$NON-NLS-1$
			if (ps[0].equals("hex")) { //$NON-NLS-1$
				int hexNo = Integer.parseInt(ps[1]);
				if (hexNo != 0) {
					Point p = new Point(hexNo / 100, hexNo % 100);
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), p, null));
				}
			} else if (ps[0].equals("turn")) { //$NON-NLS-1$
				int turnNo = Integer.parseInt(ps[1]);
				GameHolder.instance().getGame().setCurrentTurn(turnNo);
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, null));
			}
		}
	}

	@Override
	protected String getReportContents() {
		if (!GameHolder.hasInitializedGame())
			return ""; //$NON-NLS-1$
		String ret = "<html><body><div style='font-family:Tahoma; font-size:11pt'><table><tr><td width=700>"; //$NON-NLS-1$

		String charFilter = this.charName.getText();
		String textFilter = this.text.getText();
		String turnFromFilter = this.turnFrom.getText();
		String turnToFilter = this.turnTo.getText();
		String nationFilter = this.nationNo.getText();

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
			ret += "<b><u>" + Messages.getString("OrderResultsView.TurnN",new Object[] {i}) + "</u></b><p/>"; //$NON-NLS-1$ //$NON-NLS-2$
			for (Character c : t.getCharacters()) {
				if (nationValue > -1 && !c.getNationNo().equals(nationValue))
					continue;
				if (!c.getName().contains(charFilter))
					continue;
				String cor = c.getCleanOrderResults();
				if (cor == null || cor.equals("") || !cor.contains(textFilter)) //$NON-NLS-1$
					continue;
				cor = cor.replace("/", "-"); //$NON-NLS-1$ //$NON-NLS-2$
				if (textFilter.length() > 0) {
					cor = cor.replace(textFilter, "<font bgcolor=yellow>" + textFilter + "</font>"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				ret += "<b>" + c.getName() + "&nbsp;&nbsp;(" + c.getNation().getShortName() + ",&nbsp;" + c.getStatString() + ") @ <a href='http://www.event.test?turn=" + i + "&hex=" + c.getHexNo() + "'>" + c.getHexNo() + "</a></b><br/>" + cor + "<p/><p/>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			}
		}
		return ret;
	}

}
