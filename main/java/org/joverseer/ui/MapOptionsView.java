package org.joverseer.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.*;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationMapRange;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 3:09:50 μμ
 * To change this template use File | Settings | File Templates.
 */
public class MapOptionsView extends AbstractView implements ApplicationListener {
    JComboBox cmbTurns;
    JComboBox cmbMaps;

    protected JComponent createControl() {
        GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
        JLabel label;
        lb.append(label = new JLabel("Turn : "));
        label.setPreferredSize(new Dimension(50, 16));
        lb.append(cmbTurns = new JComboBox());

        cmbTurns.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object obj = cmbTurns.getSelectedItem();
                if (obj == null) return;
                int turnNo = (Integer) obj;

                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                g.setCurrentTurn(turnNo);

                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
                if (MapPanel.instance().getSelectedHex() != null) {
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
                }
            }
        });
        cmbTurns.setPreferredSize(new Dimension(100, 16));
        lb.nextLine();

        lb.append(label = new JLabel("Map : "));
        lb.append(cmbMaps = new JComboBox());
        cmbMaps.setPreferredSize(new Dimension(120, 16));
        cmbMaps.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object obj = cmbMaps.getSelectedItem();
                if (obj == null) return;
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                String str = obj.toString();
                if (str.equals("None")) {
                    mapOptions.put(MapOptionsEnum.NationMap, null);
                } else if (str.equals("Dark Servants")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapDarkServants);
                } else if (str.equals("Free People")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapFreePeople);
                } else if (str.equals("Neutrals")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNeutrals);
                } else {
                    int nationNo = g.getMetadata().getNationByName(str).getNumber();
                    mapOptions.put(MapOptionsEnum.NationMap, String.valueOf(nationNo));
                }
                int turnNo = g.getCurrentTurn();
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
            }

        });

        resetGame();
        JPanel panel = lb.getPanel();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(panel, "align=left");
        return tlb.getPanel();
    }

    public void resetGame() {
        cmbTurns.removeAllItems();
        cmbMaps.removeAllItems();
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g != null) {
            ActionListener[] als = cmbTurns.getActionListeners();
            for (ActionListener al : als) {
                cmbTurns.removeActionListener(al);
            }
            for (int i = 0; i <= g.getMaxTurn(); i++) {
                if (g.getTurn(i) != null) {
                    cmbTurns.addItem(g.getTurn(i).getTurnNo());
                }
            }
            for (ActionListener al : als) {
                cmbTurns.addActionListener(al);
            }
            cmbTurns.setSelectedItem(g.getCurrentTurn());
            cmbMaps.addItem("None");
            cmbMaps.addItem("Free People");
            cmbMaps.addItem("Dark Servants");
            cmbMaps.addItem("Neutrals");
            for (NationMapRange nmr : (ArrayList<NationMapRange>)g.getMetadata().getNationMapRanges().getItems()) {
                Nation n = g.getMetadata().getNationByNum(nmr.getNationNo());
                cmbMaps.addItem(n.getName());
            }
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                resetGame();
            }
        }
    }


}
