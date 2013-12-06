package org.joverseer.ui.pdfFileViewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PdfTurnText;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * View that shows the contents of the pdf turn files
 * 
 * @author Marios Skounakis
 */
public class PdfFileViewer extends AbstractView implements ApplicationListener {

    String searchString = null;
    int lastSearchIdx = 0;
    JTextArea text;
    JComboBox nationCombo;
    JPanel panel;
    JTextField search;

    @Override
	protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();

        TableLayoutBuilder lb = new TableLayoutBuilder();
        lb.cell(this.nationCombo = new JComboBox(), "align=left");
        this.nationCombo.setPreferredSize(new Dimension(200, 24));
        this.nationCombo.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                Game g = GameHolder.instance().getGame();
                if (PdfFileViewer.this.nationCombo.getSelectedItem() == null)
                    return;
                Nation n = g.getMetadata().getNationByName(PdfFileViewer.this.nationCombo.getSelectedItem().toString());
                PdfTurnText ptt = (PdfTurnText) g.getTurn().getContainer(TurnElementsEnum.PdfText).findFirstByProperty(
                        "nationNo", n.getNumber());
                if (ptt == null) {
                    PdfFileViewer.this.text.setText("");
                } else {
                    PdfFileViewer.this.text.setText(ptt.getText());
                    PdfFileViewer.this.text.setCaretPosition(0);
                    PdfFileViewer.this.lastSearchIdx = 0;
                }


            }
        });

        lb.gapCol();
        JLabel lbl = new JLabel("Find :");
        lb.cell(lbl);
        lb.gapCol();
        this.search = new JTextField();
        this.search.setPreferredSize(new Dimension(100, 20));
        lb.cell(this.search);
        lb.gapCol();
        JButton btnSearch = new JButton("Next");
        lb.cell(btnSearch);
        btnSearch.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                doSearch();
            }

        });

        tlb.cell(lb.getPanel(), "align=left");
        tlb.row();

        this.text = new JTextArea();
        // text.setLineWrap(true);
        // text.setWrapStyleWord(true);
        this.text.setFont(GraphicUtils.getFont("Monaco ", Font.PLAIN, 11));
        tlb.cell(new JScrollPane(this.text));

        this.panel = tlb.getPanel();
        this.panel.setPreferredSize(new Dimension(1200, 1200));
        return this.panel;

    }

    private void loadNationCombo() {
        this.nationCombo.removeAllItems();
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g))
            return;
        if (g.getTurn() == null)
            return;
        for (Nation n : (ArrayList<Nation>) g.getMetadata().getNations()) {
            NationEconomy ne = (NationEconomy) g.getTurn().getContainer(TurnElementsEnum.NationEconomy)
                    .findFirstByProperty("nationNo", n.getNumber());
            // load only nations for which economy has been imported
            if (ne == null)
                continue;
            this.nationCombo.addItem(n.getName());
        }
        if (this.nationCombo.getItemCount() > 0) {
            this.nationCombo.setSelectedIndex(0);
        }
    }

    @Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                loadNationCombo();
                initSearch();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                loadNationCombo();
                initSearch();
            }
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
