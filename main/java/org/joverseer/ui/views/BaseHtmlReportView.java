package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class BaseHtmlReportView extends AbstractView implements ApplicationListener {
	JEditorPane editor;

	protected String getReportContents() {
		return null;
	}

	protected void handleHyperlinkEvent(String url) {

	}

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		JButton btn = new JButton("Generate");
		btn.setText("Generate");
		btn.setPreferredSize(new Dimension(100, 20));
		tlb.cell(btn, "align=left");
		btn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (!GameHolder.hasInitializedGame()) {
					editor.setText("");
					return;
				}
				editor.setText(getReportContents());
			}

		});
		tlb.relatedGapRow();

		editor = new JEditorPane();
		editor.setEditable(false);
		editor.setContentType("text/html");
		editor.setText("");
		editor.addHyperlinkListener(new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED) {
					String s = e.getURL().toString();
					handleHyperlinkEvent(s);
				}
			}

		});
		tlb.cell(editor);
		JScrollPane scp = new JScrollPane(tlb.getPanel());
		scp.setPreferredSize(new Dimension(240, 1500));
		scp.getVerticalScrollBar().setUnitIncrement(32);
		return scp;
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString()) || e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				editor.setText("");
			}
		}
	}
}
