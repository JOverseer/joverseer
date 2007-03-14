package org.joverseer.ui;

import org.springframework.richclient.progress.ProgressMonitor;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.binding.form.FormModel;

import javax.swing.*;

import java.awt.*;


public class JOverseerClientProgressMonitor extends AbstractForm implements ProgressMonitor {
    public static final String FORM_PAGE = "jOverseerClientProgressMonitor";
    JLabel taskName;
    JProgressBar taskProgress;
    JList taskSubtasks;
    ListListModel llm;
    JPanel panel;
    int progressMax = 100;
    int workBuffer;
    int lastWork = 0;
    JScrollPane scp; 
    
    public JOverseerClientProgressMonitor(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        // todo fix layout
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(taskName = new JLabel());
        taskName.setPreferredSize(new Dimension(200, 16));
        tlb.row();
        tlb.separator("");
        tlb.row();
        tlb.cell(taskProgress = new JProgressBar());
        taskProgress.setMaximum(progressMax);
        tlb.row();
        tlb.cell(new JLabel(""));
        tlb.row();
        tlb.cell(new JLabel("Import steps"));
        tlb.row();
        taskSubtasks = new JList();
        taskSubtasks.setModel(llm = new ListListModel());
        taskSubtasks.setCellRenderer(new ProgressItemRenderer());
        scp = new JScrollPane(taskSubtasks);
        scp.setPreferredSize(new Dimension(320, 250));
        scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tlb.cell(scp);
        panel = tlb.getPanel();
        panel.setPreferredSize(new Dimension(350, 300));
        return panel;
    }

    private JButton getOkButton() {
        return this.getDefaultButton();
    }

    public void done() {
        getOkButton().setEnabled(true);
    }

    public boolean isCanceled() {
        return false;
    }

    public void setCanceled(boolean b) {
        // do nothing, not allowed
    }

    public void subTaskStarted(String string) {
        llm.add(string);
        int selIndex = llm.size() - 1;
        taskSubtasks.setSelectedIndex(selIndex);
        taskSubtasks.ensureIndexIsVisible(selIndex);
        panel.updateUI();
    }

    public void taskStarted(String string, int i) {
        getOkButton().setEnabled(false);
        taskProgress.setMaximum(i);
        taskName.setText(string);
        panel.updateUI();
    }

    public void worked(int i) {
        if (lastWork > i) {
            lastWork = 0;
        }
        if (workBuffer > i) {
            workBuffer = workBuffer + i - lastWork;
            lastWork = i;
        } else {
            workBuffer = i;
        }
        taskProgress.setValue(workBuffer);
        panel.updateUI();
    }
    
    public class ProgressItemRenderer extends DefaultListCellRenderer implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            JLabel lbl = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, hasFocus());
            if (value.toString().startsWith("Error")) {
                lbl.setForeground(Color.RED);
            }
            return lbl;
        }
        
    }
}
