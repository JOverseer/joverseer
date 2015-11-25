package org.joverseer.ui.views;

import org.joverseer.tools.GameEncounterReportCollector;

public class GameEncounterReportView extends BaseHtmlReportView {

	@Override
	protected String getReportContents() {
		GameEncounterReportCollector gerc = new GameEncounterReportCollector();
		return gerc.CollectEncounters();
	}

	@Override
	protected void handleHyperlinkEvent(String url) {
	}

	// JTextField charName;
	// JTextField text;
	// JTextField turnFrom;
	// JTextField turnTo;
	// JTextField nationNo;
	//
	// @Override
	// protected JPanel getCriteriaPanel() {
	// TableLayoutBuilder tlb = new TableLayoutBuilder();
	// tlb.cell(new JLabel("Character"));
	// tlb.gapCol();
	// tlb.cell(charName = new JTextField(), "colspec=left:100");
	// charName.setPreferredSize(new Dimension(100, 20));
	// tlb.relatedGapRow();
	// tlb.cell(new JLabel("Text"));
	// tlb.gapCol();
	// tlb.cell(text = new JTextField(), "colspec=left:100");
	// text.setPreferredSize(new Dimension(100, 20));
	// tlb.relatedGapRow();
	// tlb.cell(new JLabel("From turn"));
	// tlb.gapCol();
	// tlb.cell(turnFrom = new JTextField(), "colspec=left:100");
	// turnFrom.setPreferredSize(new Dimension(100, 20));
	// tlb.relatedGapRow();
	// tlb.cell(new JLabel("To turn"));
	// tlb.gapCol();
	// tlb.cell(turnTo = new JTextField(), "colspec=left:100");
	// turnTo.setPreferredSize(new Dimension(100, 20));
	// tlb.relatedGapRow();
	// tlb.cell(new JLabel("Nation"));
	// tlb.gapCol();
	// tlb.cell(nationNo = new JTextField(), "colspec=left:100");
	// nationNo.setPreferredSize(new Dimension(100, 20));
	// tlb.relatedGapRow();
	// return tlb.getPanel();
	// }

}
