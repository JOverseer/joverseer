package org.joverseer.ui.pdfFileViewer; 

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class PdfFileViewer extends AbstractView implements ApplicationListener {
    JTextArea text;
    JComboBox nationCombo;
    JPanel panel;
    JTextField search;
    
    protected JComponent createControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(nationCombo = new JComboBox(), "align=left");
        nationCombo.setPreferredSize(new Dimension(200, 24));
        nationCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game g = GameHolder.instance().getGame();
                if (nationCombo.getSelectedItem() == null) return;
                Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
                PdfTurnText ptt = (PdfTurnText)g.getTurn().getContainer(TurnElementsEnum.PdfText).findFirstByProperty("nationNo", n.getNumber());
                if (ptt == null) {
                    text.setText("");
                } else {
                    text.setText(ptt.getText());
                    text.setCaretPosition(0);
                }
                
                
            }
        });
        tlb.row();
        
        text = new JTextArea();
        //text.setLineWrap(true);
        //text.setWrapStyleWord(true);
        //text.setFont(GraphicUtils.getFont("Courier New", Font.PLAIN, 11));
        tlb.cell(new JScrollPane(text));
        
        panel = tlb.getPanel();
        panel.setPreferredSize(new Dimension(1200, 1200));
        return panel;
        
    }
    
    private void loadNationCombo() {
        nationCombo.removeAllItems();
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return;
        if (g.getTurn() == null) return;
        for (Nation n : (ArrayList<Nation>)g.getMetadata().getNations()) {
            NationEconomy ne = (NationEconomy)g.getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber());
            // load only nations for which economy has been imported
            if (ne == null) continue;
            nationCombo.addItem(n.getName());
        }
        if (nationCombo.getItemCount() > 0) {
            nationCombo.setSelectedIndex(0);
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                loadNationCombo();
            }
        }
    }
    

}
