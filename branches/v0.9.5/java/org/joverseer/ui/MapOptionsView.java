package org.joverseer.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.map.MapMetadata;
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
    JComboBox zoom;
    JComboBox hexGraphics;
    JCheckBox drawOrders;
    JCheckBox drawNamesOnOrders;
    JCheckBox showClimate;
    
    boolean fireEvents = true;

	protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();
        JLabel label;
        lb.cell(label = new JLabel("Turn : "), "colspec=left:130px");
        label.setPreferredSize(new Dimension(100, 16));
        lb.cell(cmbTurns = new JComboBox(), "colspec=left:100px");
        lb.relatedGapRow();

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
        cmbTurns.setPreferredSize(new Dimension(100, 16));
        lb.row();

        //lb.append(new JLabel("  "));
        lb.cell(label = new JLabel("Map : "));
        lb.cell(cmbMaps = new JComboBox(), "align=left");
        lb.relatedGapRow();
        cmbMaps.setPreferredSize(new Dimension(100, 16));
        cmbMaps.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!fireEvents) return;
                Object obj = cmbMaps.getSelectedItem();
                if (obj == null) return;
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                String str = obj.toString();
                if (str.equals("Current")) {
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
        lb.row();
        
        //lb.append(new JLabel("  "));
        lb.cell(label = new JLabel("Draw orders : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.cell(drawOrders = new JCheckBox(), "align=left");
        lb.relatedGapRow();
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
        lb.row();
        
        lb.cell(label = new JLabel("Draw names on orders : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.cell(drawNamesOnOrders = new JCheckBox(), "align=left");
        lb.relatedGapRow();
        drawNamesOnOrders.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                if (drawNamesOnOrders.getModel().isSelected()) {
                    mapOptions.put(MapOptionsEnum.DrawNamesOnOrders, MapOptionValuesEnum.DrawNamesOnOrdersOn);
                } else {
                    mapOptions.put(MapOptionsEnum.DrawNamesOnOrders, MapOptionValuesEnum.DrawNamesOnOrdersOff);
                }
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (!Game.isInitialized(g)) return;
                int turnNo = g.getCurrentTurn();
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), turnNo, this));
                
            }
            
        });
        lb.row();
        //lb.append(new JLabel("  "));
        lb.cell(label = new JLabel("Show climate : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.cell(showClimate = new JCheckBox(), "align=left");
        lb.relatedGapRow();
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
        lb.row();
        lb.cell(label = new JLabel("Zoom level : "));
        ZoomOption[] zoomOptions = new ZoomOption[]{
                new ZoomOption("1", 13, 13),
                new ZoomOption("2", 15, 15),
                new ZoomOption("3", 17, 17),
                new ZoomOption("4", 19, 19),
        };
        lb.cell(zoom = new JComboBox(zoomOptions), "align=left");
        lb.relatedGapRow();
        zoom.setPreferredSize(new Dimension(100, 16));
        zoom.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ZoomOption opt = (ZoomOption)zoom.getSelectedItem();
                if (opt == null) return;
                MapMetadata metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
                metadata.setGridCellHeight(opt.getHeight());
                metadata.setGridCellWidth(opt.getWidth());
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));
            }
        });
        
        lb.row();
        lb.cell(label = new JLabel("Terrain graphics : "));
        lb.cell(hexGraphics = new JComboBox(new String[]{"Simple", "Texture"}), "align=left");
        hexGraphics.setSelectedIndex(1);
        lb.relatedGapRow();
        hexGraphics.setPreferredSize(new Dimension(100, 16));
        final Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
        hexGraphics.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String opt = (String)hexGraphics.getSelectedItem();
                if (opt == null) return;
                prefs.put("hexGraphics", opt);
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");                
                if (opt.equals("Simple")) {
                	mapOptions.put(MapOptionsEnum.HexGraphics, MapOptionValuesEnum.HexGraphicsSimple);                	
                } else if (opt.equals("Texture")) {
                	mapOptions.put(MapOptionsEnum.HexGraphics, MapOptionValuesEnum.HexGraphicsTexture);                	
                };
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));
            }
        });
        
        String hexGraphicsOpt = prefs.get("hexGraphics", null);
        if (hexGraphicsOpt != null) {
            hexGraphics.setSelectedItem(hexGraphicsOpt);
        }

        resetGame();
        JPanel panel = lb.getPanel();
        panel.setPreferredSize(new Dimension(130, 200));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        
        return new JScrollPane(panel);
//        TableLayoutBuilder tlb = new TableLayoutBuilder();
//        tlb.cell(panel, "align=left");
//        tlb.gapCol();
//        return panel;
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
            cmbMaps.addItem("Current");
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

    class ZoomOption {
        String description;
        int width;
        int height;
        public ZoomOption(String description, int width, int height) {
            super();
            this.description = description;
            this.width = width;
            this.height = height;
        }
        
        public String toString() {
            return description;
        }

        
        public String getDescription() {
            return description;
        }

        
        public int getHeight() {
            return height;
        }

        
        public int getWidth() {
            return width;
        }
        
        
    }
}
