package org.joverseer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.joverseer.ui.support.Messages;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.progress.ProgressMonitor;

/**
 * A form that implements a Progress Monitor. Used for showing the progress for
 * importing xml and pdf files
 * 
 * @author Marios Skounakis
 */
public class JOverseerClientProgressMonitor extends AbstractForm implements ProgressMonitor {
	public static final String FORM_PAGE = "jOverseerClientProgressMonitor"; //$NON-NLS-1$
	JLabel taskName;
	JProgressBar taskProgress;
	JList taskSubtasks;
	ListListModel llm;
	JPanel panel;
	int progressMax = 100;
	int workBuffer;
	int lastWork = 0;
	JScrollPane scp;
	JTextArea globalMessage;

	public JOverseerClientProgressMonitor(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		// todo fix layout
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(this.taskName = new JLabel());
		this.taskName.setPreferredSize(new Dimension(350, 16));
		tlb.row();
		tlb.relatedGapRow();
		tlb.cell(this.taskProgress = new JProgressBar());
		this.taskProgress.setMaximum(this.progressMax);
		tlb.row();
		tlb.relatedGapRow();
		tlb.cell(new JLabel(Messages.getString("jOverseerClientProgressMonitor.ImportSteps")));  //$NON-NLS-1$
		tlb.row();
		this.taskSubtasks = new JList();
		this.taskSubtasks.setModel(this.llm = new ListListModel());
		this.taskSubtasks.setCellRenderer(new ProgressItemRenderer());
		this.scp = new JScrollPane(this.taskSubtasks);
		this.scp.setPreferredSize(new Dimension(350, 350));
		this.scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tlb.cell(this.scp);
		tlb.relatedGapRow();
		tlb.row();
		this.globalMessage = new JTextArea();
		this.globalMessage.setWrapStyleWord(true);
		this.globalMessage.setLineWrap(true);
		@SuppressWarnings("hiding")
		JScrollPane scp = new JScrollPane(this.globalMessage);
		tlb.cell(scp);
		scp.setPreferredSize(new Dimension(350, 80));

		this.panel = tlb.getPanel();
		this.panel.setPreferredSize(new Dimension(390, 300));
		return this.panel;
	}

	private JButton getOkButton() {
		return this.getDefaultButton();
	}

	@Override
	public void done() {
		try {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					getOkButton().setEnabled(true);
				}
			};
			SwingUtilities.invokeLater(r);
		} catch (Exception exc) {
			this.logger.error(exc);
		}
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void setCanceled(boolean b) {
		// do nothing, not allowed
	}

	@Override
	public void subTaskStarted(String string) {
		final String str = string;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				JOverseerClientProgressMonitor.this.llm.add(str);
				int selIndex = JOverseerClientProgressMonitor.this.llm.size() - 1;
				JOverseerClientProgressMonitor.this.taskSubtasks.setSelectedIndex(selIndex);
				JOverseerClientProgressMonitor.this.taskSubtasks.ensureIndexIsVisible(selIndex);
				JOverseerClientProgressMonitor.this.taskSubtasks.updateUI();
				JOverseerClientProgressMonitor.this.panel.updateUI();
			}
		};
		SwingUtilities.invokeLater(r);
	}

	@Override
	public void taskStarted(String string, int i) {
		try {
			final String str = string;
			final int ii = i;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					getOkButton().setEnabled(false);
					JOverseerClientProgressMonitor.this.taskProgress.setMaximum(ii);
					JOverseerClientProgressMonitor.this.taskName.setText(str);
					JOverseerClientProgressMonitor.this.panel.updateUI();
				}
			};
			SwingUtilities.invokeLater(r);
		} catch (Exception exc) {
			this.logger.error(exc);
		}
	}

	public void setGlobalMessage(String msg) {
		try {
			final String str = msg;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					JOverseerClientProgressMonitor.this.globalMessage.setText(str);
					JOverseerClientProgressMonitor.this.panel.updateUI();
				}
			};
			SwingUtilities.invokeLater(r);
		} catch (Exception exc) {
			this.logger.error(exc);
		}
	}

	/**
	 * Hack so that you can use multiple tasks with the same progress monitor
	 * The idea is that if the task sets worked to a value smaller than the
	 * current value, the new value is added E.g: - worked = 50, ok - worked =
	 * 100, ok - new process starts - worked = 50 --> make progress = 100 + 50
	 * -> 150 - worked = 100 --> make progress = 150 + 100 - 50 -> 200
	 */
	@Override
	public void worked(int ii) {
		try {
			final int i = ii;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (JOverseerClientProgressMonitor.this.lastWork > i) {
						JOverseerClientProgressMonitor.this.lastWork = 0;
					}
					if (JOverseerClientProgressMonitor.this.workBuffer > i) {
						JOverseerClientProgressMonitor.this.workBuffer = JOverseerClientProgressMonitor.this.workBuffer + i - JOverseerClientProgressMonitor.this.lastWork;
						JOverseerClientProgressMonitor.this.lastWork = i;
					} else {
						JOverseerClientProgressMonitor.this.workBuffer = i;
					}
					JOverseerClientProgressMonitor.this.taskProgress.setValue(JOverseerClientProgressMonitor.this.workBuffer);
					JOverseerClientProgressMonitor.this.panel.updateUI();
				}
			};
			SwingUtilities.invokeLater(r);
		} catch (Exception exc) {
			this.logger.error(exc);
		}
	}

	/**
	 * Specialized renderer that renders items that start with the "Error"
	 * string with red font
	 * 
	 * @author Marios Skounakis
	 */
	public class ProgressItemRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus());
			if (value != null && value.toString().startsWith("Error")) { //$NON-NLS-1$
				lbl.setForeground(Color.RED);
			}
			return lbl;
		}

	}
}
