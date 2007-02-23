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


public class MapOptionsView extends AbstractView implements ApplicationListener {
    JComboBox cmbTurns;
    JComboBox cmbMaps;
    JCheckBox drawOrders;
    JCheckBox showClimate;
    
    boolean fireEvents = true;

    protected JComponent createControl() {
        GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
        lb.setDefaultInsets(new Insets(5, 0, 5, 0));
        JLabel label;
        lb.append(label = new JLabel("Turn : "));
        //label.setPreferredSize(new Dimension(50, 16));
        lb.append(cmbTurns = new JComboBox());

        cmbTurns.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!fireEvents) return;
                Object obj = cmbTurns.getSelectedItem();
                if (obj == null) return;
                int turnNo = (Integer) obj;

                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (g.getCurrentTurn() == turnNo) return;
                g.setCurrentTurn(turnNo);

                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
                if (MapPanel.instance().getSelectedHex() != null) {
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
                }
            }
        });
        cmbTurns.setPreferredSize(new Dimension(60, 16));
        lb.nextLine();

        //lb.append(new JLabel("  "));
        lb.append(label = new JLabel("Map : "));
        lb.append(cmbMaps = new JComboBox());
        cmbMaps.setPreferredSize(new Dimension(100, 16));
        cmbMaps.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!fireEvents) return;
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
        lb.nextLine();
        
        //lb.append(new JLabel("  "));
        lb.append(label = new JLabel("Draw orders : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.append(drawOrders = new JCheckBox());
        drawOrders.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                if (drawOrders.getModel().isSelected()) {
                    mapOptions.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOn);
                } else {
                    mapOptions.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOff);
                }
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (!Game.isInitialized(g)) return;
                int turnNo = g.getCurrentTurn();
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), turnNo, this));
                
            }
            
        });
        lb.nextLine();
        //lb.append(new JLabel("  "));
        lb.append(label = new JLabel("Show climate : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.append(showClimate = new JCheckBox());
        showClimate.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                if (showClimate.getModel().isSelected()) {
                    mapOptions.put(MapOptionsEnum.ShowClimate, MapOptionValuesEnum.ShowClimateOn);
                } else {
                    mapOptions.put(MapOptionsEnum.ShowClimate, MapOptionValuesEnum.ShowClimateOff);
                }
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (!Game.isInitialized(g)) return;
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
        fireEvents = false;
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
        fireEvents = true;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                fireEvents = false;
                resetGame();
                fireEvents = true;
            }
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                fireEvents = false;
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (Game.isInitialized(g)) {
                    if (!cmbTurns.getSelectedItem().equals(g.getCurrentTurn())) {
                        cmbTurns.setSelectedItem(g.getCurrentTurn());
                    }
                }
                fireEvents = true;
            }
        }
    }


}