package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.ScalableAbstractView;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class BaseHtmlReportView extends ScalableAbstractView implements ApplicationListener {
	// injected dependency
	GameHolder gameHolder;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	public BaseHtmlReportView() {
		this.hideButton = false;
	}
	public BaseHtmlReportView(boolean hideButton) {
		super();
		this.hideButton = hideButton;
	}

	JEditorPane editor;
	private boolean hideButton;
	private boolean isHideButton() {
		return this.hideButton;
	}

	boolean isReportGenerated = false;
	protected String getReportContents() {
		this.isReportGenerated = true;
		return null;
	}

	@Override
	public void componentFocusGained() {
		super.componentFocusGained();
		if (!this.isReportGenerated) {
			this.editor.setText(this.getReportContents());
		}
	}

	protected void handleHyperlinkEvent(String url) {

	}

	protected JPanel getCriteriaPanel() {
		return null;
	}

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.relatedGapRow();
		JPanel pnl = getCriteriaPanel();
		if (pnl != null) {
			tlb.cell(pnl, "align=left"); //$NON-NLS-1$
			tlb.relatedGapRow();
		}
		if (!this.isHideButton()) {
			JButton btn = new JButton(Messages.getString("BaseHtmlReportView.Generate")); //$NON-NLS-1$
			btn.setText(Messages.getString("BaseHtmlReportView.Generate")); //$NON-NLS-1$
			btn.setPreferredSize(this.uiSizes.newDimension(100/20, this.uiSizes.getHeight5()));
			tlb.cell(btn, "align=left"); //$NON-NLS-1$
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!BaseHtmlReportView.this.gameHolder.isGameInitialized()) {
						BaseHtmlReportView.this.editor.setText(""); //$NON-NLS-1$
						BaseHtmlReportView.this.isReportGenerated = false;
						return;
					}
					BaseHtmlReportView.this.editor.setText(getReportContents());
				}

			});
			tlb.relatedGapRow();

		}
		this.editor = new JEditorPane();
		this.editor.setEditable(false);
		this.editor.setContentType("text/html"); //$NON-NLS-1$
		this.editor.setText(""); //$NON-NLS-1$
		this.editor.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED) {
					String s = e.getURL().toString();
					handleHyperlinkEvent(s);
				}
			}

		});
		tlb.cell(this.editor);
		JScrollPane scp = new JScrollPane(tlb.getPanel());
		scp.setPreferredSize(new Dimension(240, 1500));
		scp.getVerticalScrollBar().setUnitIncrement(32);
		return scp;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case SelectedTurnChangedEvent:
		case GameChangedEvent:
			this.isReportGenerated = false; // report gets generated on focus change.
			this.editor.setText(this.getReportContents());
		}
	}
}
