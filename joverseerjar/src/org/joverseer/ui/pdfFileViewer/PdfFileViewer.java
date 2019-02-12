package org.joverseer.ui.pdfFileViewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.joverseer.domain.PdfTurnText;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.ui.BaseView;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.NationComboBox;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * View that shows the contents of the pdf turn files
 *
 * @author Marios Skounakis
 */
public class PdfFileViewer extends BaseView implements ApplicationListener {

    String searchString = null;
    int lastSearchIdx = 0;
    JTextArea text;
    NationComboBox nationCombo;
    JPanel panel;
    JTextField search;

    @Override
	protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();

        TableLayoutBuilder lb = new TableLayoutBuilder();
        lb.cell(this.nationCombo = new NationComboBox(this.getGameHolder()), "align=left"); //$NON-NLS-1$
        this.nationCombo.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                Nation n = PdfFileViewer.this.nationCombo.getSelectedNation();
                PdfTurnText ptt = null;
                if (n != null) {
                	Turn t = PdfFileViewer.this.getTurn();
                	if (t != null) {
                		ptt = (PdfTurnText) t.getContainer(TurnElementsEnum.PdfText).findFirstByProperty(
                				"nationNo", n.getNumber()); //$NON-NLS-1$
                	}
                }
                if (ptt == null) {
                    PdfFileViewer.this.text.setText("No PDF text for nation found."); //$NON-NLS-1$
                } else {
                    PdfFileViewer.this.text.setText(ptt.getText());
                    PdfFileViewer.this.text.setCaretPosition(0);
                    PdfFileViewer.this.lastSearchIdx = 0;
                }


            }
        });

        lb.gapCol();
        JLabel lbl = new JLabel(Messages.getString("PdfFileViewer.FindColon")); //$NON-NLS-1$
        lb.cell(lbl);
        lb.gapCol();
        this.search = new JTextField();
        this.search.setPreferredSize(new Dimension(100, 20));
        lb.cell(this.search);
        lb.gapCol();
        JButton btnSearch = new JButton(Messages.getString("PdfFileViewer.Next")); //$NON-NLS-1$
        lb.cell(btnSearch);
        btnSearch.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                doSearch();
            }

        });

        tlb.cell(lb.getPanel(), "align=left"); //$NON-NLS-1$
        tlb.row();

        this.text = new JTextArea();
        // text.setLineWrap(true);
        // text.setWrapStyleWord(true);
        this.text.setFont(GraphicUtils.getFont("Monaco ", Font.PLAIN, 11)); //$NON-NLS-1$
        tlb.cell(new JScrollPane(this.text));

        this.panel = tlb.getPanel();
        this.panel.setPreferredSize(new Dimension(1200, 1200));
        return this.panel;

    }

    @Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            this.onJOEvent((JOverseerEvent) applicationEvent);
        }
    }
    public void onJOEvent(JOverseerEvent e) {
    	switch(e.getType()) {
    	case GameChangedEvent:
        	super.resetGame();
            this.nationCombo.load(true, true);
            initSearch();
            break;
    	case SelectedTurnChangedEvent:
            this.nationCombo.load(true, true);
            initSearch();
            break;
        }

    }

    private void initSearch() {
        this.searchString = null;
        this.lastSearchIdx = 0;
    }

    private void doSearch() {
        if (!this.search.getText().equals(this.searchString)) {
            this.lastSearchIdx = 0;
            this.searchString = this.search.getText();
        }
        int i = this.text.getText().indexOf(this.searchString, this.lastSearchIdx + this.searchString.length());
        if (i > -1) {
            this.lastSearchIdx = i;
            this.text.setCaretPosition(this.lastSearchIdx);
            this.text.setSelectionStart(this.lastSearchIdx);
            this.text.setSelectionEnd(this.lastSearchIdx + this.searchString.length());
            this.text.requestFocusInWindow();
        } else {
            this.lastSearchIdx = 0;
            this.text.setSelectionStart(-1);
            this.text.setSelectionEnd(-1);
        }
    }

}
