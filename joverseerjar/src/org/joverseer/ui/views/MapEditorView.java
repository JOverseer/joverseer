package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapEditor.MapEditorOptionsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class MapEditorView extends AbstractView implements ApplicationListener {

	JTextArea log;

	public static MapEditorView instance = null;

	public MapEditorView() {
		instance = this;
	}

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel(Messages.getString("MapEditorView.ActiveColon")), "colspec=left:70px"); //$NON-NLS-1$ //$NON-NLS-2$
		final JCheckBox active = new JCheckBox();
		active.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap mapEditorOptions = (HashMap) Application.instance().getApplicationContext().getBean("mapEditorOptions"); //$NON-NLS-1$
				mapEditorOptions.put(MapEditorOptionsEnum.active, active.isSelected());
			}
		});
		tlb.gapCol();
		tlb.cell(active, "colspec=left:20px"); //$NON-NLS-1$

		lb.cell(tlb.getPanel());
		lb.gapCol();
		lb.cell(new JLabel(" "), "colspec=left:100px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.gapCol();
		lb.cell(new JLabel(" "), "colspec=left:100px"); //$NON-NLS-1$ //$NON-NLS-2$
		lb.row();
		lb.cell(new JSeparator(), "colspan=5"); //$NON-NLS-1$
		lb.row();

		ButtonGroup grp = new ButtonGroup();

		tlb = new TableLayoutBuilder();
		for (HexTerrainEnum t : HexTerrainEnum.values()) {
			tlb.cell(new JLabel(t.toString()), "colspec=left:70px"); //$NON-NLS-1$
			tlb.gapCol();
			final JRadioButton rb = new JRadioButton();
			final HexTerrainEnum te = t;
			rb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					HashMap mapEditorOptions = (HashMap) Application.instance().getApplicationContext().getBean("mapEditorOptions"); //$NON-NLS-1$
					mapEditorOptions.put(MapEditorOptionsEnum.brush, te);
				}
			});
			tlb.cell(rb, "colspec=left:20px"); //$NON-NLS-1$
			grp.add(rb);
			tlb.row();
		}

		lb.cell(tlb.getPanel());

		tlb = new TableLayoutBuilder();
		for (HexSideElementEnum e : HexSideElementEnum.values()) {
			tlb.cell(new JLabel(e.toString()), "colspec=left:70px"); //$NON-NLS-1$
			tlb.gapCol();
			final HexSideElementEnum se = e;
			final JRadioButton rb = new JRadioButton();
			rb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e1) {
					HashMap mapEditorOptions = (HashMap) Application.instance().getApplicationContext().getBean("mapEditorOptions"); //$NON-NLS-1$
					mapEditorOptions.put(MapEditorOptionsEnum.brush, se);
				}
			});
			tlb.cell(rb, "colspec=left:20px"); //$NON-NLS-1$
			grp.add(rb);
			tlb.row();
		}
		lb.gapCol("20px"); //$NON-NLS-1$
		lb.cell(tlb.getPanel());

		lb.gapCol("20px"); //$NON-NLS-1$
		this.log = new JTextArea();
		JScrollPane scp;
		lb.cell(scp = new JScrollPane(this.log));
		scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scp.setPreferredSize(new Dimension(150, 150));
		this.log.setEditable(false);
		this.log.setWrapStyleWord(true);
		// log.setBorder(new LineBorder(Color.black));

		JPanel pnl = lb.getPanel();
		pnl.setBorder(new EmptyBorder(5, 5, 5, 5));
		return new JScrollPane(pnl);

	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {

			}
		}
	}

	public void log(String message) {
		this.log.setText(message + "\n" + this.log.getText()); //$NON-NLS-1$
		this.log.setCaretPosition(0);
	}
}
