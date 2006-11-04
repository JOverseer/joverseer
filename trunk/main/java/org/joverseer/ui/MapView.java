package org.joverseer.ui;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.joverseer.ui.events.SelectedHexChangedListener;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.events.SelectedHexChangedEvent;
import org.joverseer.ui.viewers.PopulationCenterViewer;
import org.joverseer.ui.support.JOverseerEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 24 Σεπ 2006
 * Time: 10:22:12 μμ
 * To change this template use File | Settings | File Templates.
 */
public class MapView extends AbstractView  implements SelectedHexChangedListener, ApplicationListener {

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
        mapPanel.setPreferredSize(new Dimension(2000, 2000));
        mapPanel.addSelectedHexChangedEventListener(this);
        scp.setPreferredSize(new Dimension(800, 500));
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
            }
        }
    }

    public void eventOccured(SelectedHexChangedEvent ev) {
//        Point p = mapPanel.getSelectedHex();
//        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
//        Turn t = g.getTurn();
//        org.joverseer.support.Container c = t.getContainer(TurnElementsEnum.PopulationCenter);
//        PopulationCenter pc = (PopulationCenter)c.findFirstByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
//        if (pc != null) {
//            pcViewer.setFormObject(pc);
//            pcViewer.getControl().setVisible(true);
//            //pcViewerHolder.setVisible(true);
//        } else {
//            pcViewer.getControl().setVisible(false);
//            //pcViewerHolder.setVisible(false);
//            //pcViewerHolder.add(pcViewer.getControl());
//        }
    }


}
