package org.joverseer.ui;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.Application;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.viewers.PopulationCenterViewer;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class MapView extends AbstractView  implements ApplicationListener {

    MapPanel mapPanel;
    PopulationCenterViewer pcViewer;
    JScrollPane scp;
    /**
     * Create the actual UI control for this view. It will be placed into the window
     * according to the layout of the page holding this view.
     */
    protected JComponent createControl() {
        // In this view, we're just going to use standard Swing to place a
        // few controls.

        scp = new JScrollPane(mapPanel = new MapPanel());
        mapPanel.setFocusable(true);
        mapPanel.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_UP) {
					scp.getVerticalScrollBar().setValue(
							scp.getVerticalScrollBar().getValue() - 
							scp.getVerticalScrollBar().getBlockIncrement()
							);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
					scp.getVerticalScrollBar().setValue(
							scp.getVerticalScrollBar().getValue() + 
							scp.getVerticalScrollBar().getBlockIncrement()
							);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
					scp.getHorizontalScrollBar().setValue(
							scp.getHorizontalScrollBar().getValue() - 
							scp.getHorizontalScrollBar().getBlockIncrement()
							);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
					scp.getHorizontalScrollBar().setValue(
							scp.getHorizontalScrollBar().getValue() + 
							scp.getHorizontalScrollBar().getBlockIncrement()
							);
				}
				
			}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        mapPanel.setPreferredSize(new Dimension(3500, 2500));
        mapPanel.setBackground(Color.white);
        scp.setPreferredSize(new Dimension(800, 500));
        MapMetadata mm = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
        scp.getVerticalScrollBar().setUnitIncrement(mm.getGridCellHeight() * mm.getHexSize() * 2);
        scp.getHorizontalScrollBar().setUnitIncrement(mm.getGridCellWidth() * mm.getHexSize() * 2);
        return scp;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                mapPanel.invalidateAll();
                mapPanel.updateUI();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                if (e.getSender() != mapPanel) {
                    Point p = (Point)e.getObject();
                    mapPanel.setSelectedHex(p);
                    Rectangle shr = mapPanel.getSelectedHexRectangle();
                    mapPanel.updateUI();
                    // expand shr
                    Rectangle vr = mapPanel.getVisibleRect();
                    int w = (int)(vr.getWidth() - shr.getWidth());
                    int h = (int)(vr.getHeight() - shr.getHeight());
                    Rectangle nr = new Rectangle(shr.x - w / 2,
                                                 shr.y - h / 2,
                                                 (int)shr.getWidth() + w / 2,
                                                (int)shr.getHeight() + h);
                    if (!scp.getViewportBorderBounds().contains(nr)) {
                        mapPanel.scrollRectToVisible(nr);
                    }
                }
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                mapPanel.invalidateAll();
                mapPanel.updateUI();
            } else if (e.getEventType().equals(LifecycleEventsEnum.RefreshTurnMapItems.toString())) {
                mapPanel.invalidateAll();
                mapPanel.updateUI();
            } else if (e.getEventType().equals(LifecycleEventsEnum.RefreshMapItems.toString())) {
                mapPanel.invalidateMapItems();
                mapPanel.updateUI();
            } else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                mapPanel.invalidateMapItems();
                mapPanel.updateUI();
            } else if (e.getEventType().equals(LifecycleEventsEnum.MapMetadataChangedEvent.toString())) {
                MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
                mapPanel.setPreferredSize(new Dimension(
                            mm.getGridCellWidth() * mm.getHexSize() * (mm.getMaxMapColumn() + 1),
                            mm.getGridCellHeight() * mm.getHexSize() * mm.getMaxMapRow()
                        ));
                mapPanel.invalidateAndReset();
                mapPanel.updateUI();
                scp.updateUI();
            }
            
        }
    }

}
