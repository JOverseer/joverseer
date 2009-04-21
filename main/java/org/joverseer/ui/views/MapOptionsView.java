package org.joverseer.ui.views;

import java.applet.AppletContext;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * View for changing the map options
 * @author Marios Skounakis
 */
public class MapOptionsView extends AbstractView implements ApplicationListener {
    JComboBox cmbTurns;
    JComboBox cmbMaps;
    JComboBox zoom;
    JComboBox nationColors;
    JCheckBox drawOrders;
    JCheckBox drawNamesOnOrders;
    JCheckBox showClimate;
    JCheckBox popCenterNames;
    
    boolean fireEvents = true;

	protected JComponent createControl() {
        HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
        mapOptions.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOn);
        TableLayoutBuilder lb = new TableLayoutBuilder();
        JLabel label;
        lb.cell(label = new JLabel("Turn : "), "colspec=left:130px");
        label.setPreferredSize(new Dimension(100, 16));
        lb.cell(cmbTurns = new JComboBox(), "colspec=left:100px");
        lb.relatedGapRow();

        cmbTurns.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object obj = cmbTurns.getSelectedItem();
                if (obj == null) return;
                int turnNo = (Integer) obj;

                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (g.getCurrentTurn() == turnNo) return;
                g.setCurrentTurn(turnNo);
                if (!fireEvents) return;
                
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
                Object obj = cmbMaps.getSelectedItem();
                if (obj == null) return;
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                String str = obj.toString();
                if (str.equals("Current")) {
                    mapOptions.put(MapOptionsEnum.NationMap, null);
                } else if (str.equals("Dark Servants")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapDarkServants);
                } else if (str.equals("Not Dark Servants")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotDarkServants);
                } else if (str.equals("Free People")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapFreePeople);
                } else if (str.equals("Not Free People")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotFreePeople);
                } else if (str.equals("Neutrals")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNeutrals);
                } else if (str.equals("Not Neutrals")) {
                    mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotNeutrals);
                } else if (str.equals("None")) {
                	mapOptions.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNone);
                } else {
                    int nationNo = g.getMetadata().getNationByName(str).getNumber();
                    mapOptions.put(MapOptionsEnum.NationMap, String.valueOf(nationNo));
                }
                int turnNo = g.getCurrentTurn();
                if (!fireEvents) return;
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
            }

        });
        lb.row();
        
        //lb.append(new JLabel("  "));
        lb.cell(label = new JLabel("Draw orders : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.cell(drawOrders = new JCheckBox(), "align=left");
        drawOrders.setSelected(mapOptions.get(MapOptionsEnum.DrawOrders) != null && 
        		mapOptions.get(MapOptionsEnum.DrawOrders) == MapOptionValuesEnum.DrawOrdersOn);
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
                if (!fireEvents) return;
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
                if (!fireEvents) return;
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), turnNo, this));
                
            }
            
        });
        lb.row();
        
        lb.cell(label = new JLabel("Show PC names : "));
        //label.setPreferredSize(new Dimension(100, 16));
        lb.cell(popCenterNames = new JCheckBox(), "align=left");
        lb.relatedGapRow();
        popCenterNames.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");
                if (popCenterNames.getModel().isSelected()) {
                    mapOptions.put(MapOptionsEnum.PopCenterNames, MapOptionValuesEnum.PopCenterNamesOn);
                } else {
                    mapOptions.put(MapOptionsEnum.PopCenterNames, MapOptionValuesEnum.PopCenterNamesOff);
                }
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                if (!Game.isInitialized(g)) return;
                int turnNo = g.getCurrentTurn();
                if (!fireEvents) return;
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
                
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
                if (!fireEvents) return;
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
                
            }
            
        });
        lb.row();
        lb.cell(label = new JLabel("Zoom level : "));
        ZoomOption[] zoomOptions = new ZoomOption[]{
        		new ZoomOption("s1", 6, 6),
        		new ZoomOption("s2", 7, 7),
        		new ZoomOption("s3", 9, 9),
        		new ZoomOption("s4", 11, 11),
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
                if (!fireEvents) return;
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));
            }
        });
        zoom.setSelectedIndex(4);
        
        
        lb.row();
        lb.cell(label = new JLabel("Nation colors : "));
        lb.cell(nationColors = new JComboBox(new String[]{"Color/Nation", "Color/Allegiance"}), "align=left");
        nationColors.setSelectedIndex(0);
        lb.relatedGapRow();
        nationColors.setPreferredSize(new Dimension(100, 16));
        nationColors.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
            	 String opt = (String)nationColors.getSelectedItem();
                HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");                
                if (opt == null) return;
                if (opt.equals("Color/Nation")) {
                	mapOptions.put(MapOptionsEnum.NationColors, MapOptionValuesEnum.NationColorsNation);                	
                } else if (opt.equals("Color/Allegiance")) {
                	mapOptions.put(MapOptionsEnum.NationColors, MapOptionValuesEnum.NationColorsAllegiance);                	
                };
                if (!fireEvents) return;
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));
            }
        });
        
        
        
        resetGame();
        JPanel panel = lb.getPanel();
        panel.setPreferredSize(new Dimension(130, 200));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        
        return new JScrollPane(panel);
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
            cmbMaps.addItem("None");
            cmbMaps.addItem("Not Free People");
            cmbMaps.addItem("Not Dark Servants");
            cmbMaps.addItem("Not Neutrals");
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
            if (e.getEventType().equals(LifecycleEventsEnum.SetPalantirMapStyleEvent.toString())) {
                fireEvents = false;
                
                zoom.setSelectedIndex(2);
                nationColors.setSelectedIndex(0);
                showClimate.setSelected(false);                
                PreferenceRegistry.instance().setPreferenceValue("map.terrainGraphics", "simple");
                PreferenceRegistry.instance().setPreferenceValue("map.fogOfWarStyle", "xs");
                PreferenceRegistry.instance().setPreferenceValue("map.charsAndArmies", "simplified");
                PreferenceRegistry.instance().setPreferenceValue("map.deadCharacters", "no");
                PreferenceRegistry.instance().setPreferenceValue("map.showArmyType", "no");
                
                fireEvents = true;
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));

            }
            if (e.getEventType().equals(LifecycleEventsEnum.ZoomIncreaseEvent.toString())) {
            	if (zoom.getSelectedIndex() < zoom.getItemCount()-1) {
            		zoom.setSelectedIndex(zoom.getSelectedIndex()+1);
            	}
            }
        	if (e.getEventType().equals(LifecycleEventsEnum.ZoomDecreaseEvent.toString())) {
        		if (zoom.getSelectedIndex() > 0) {
            		zoom.setSelectedIndex(zoom.getSelectedIndex()-1);
            	}
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
