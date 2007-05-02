package org.joverseer.ui;

import org.springframework.richclient.application.support.AbstractView;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.Viewport;
import org.flexdock.view.View;
import org.joverseer.ui.map.MapPanel;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;


public class MainView extends AbstractView {
    MapPanel mapPanel;

    private JPanel create() {
        JPanel panel = new JPanel();

        Viewport viewport = new Viewport();
		panel.add(viewport, BorderLayout.CENTER);

		View startPage = createMapPanel();

		viewport.dock(startPage);
        panel.setLayout(new BorderLayout(3, 3));
        panel.setSize(500,500);
        return panel;
    }

    private static void connectToDockingPort(JComponent component, DefaultDockingPort port) {
        DockingManager.registerDockable(component);
        port.dock(component, DockingConstants.CENTER_REGION);
    }

    private static DefaultDockingPort createDockingPort() {
        DefaultDockingPort port = new DefaultDockingPort();
        port.setSingleTabAllowed(true);
        port.setPreferredSize(new Dimension(100, 100));
        return port;
    }

    private static JComponent createDockableComponent(String name) {
        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.BLUE));
        panel.add(new JLabel(name));
        return panel;
    }

    private View createMapPanel() {
        MapPanel mapPanel = new MapPanel();
        JScrollPane scp = new JScrollPane(mapPanel);
        mapPanel.setPreferredSize(new Dimension(2000, 2000));

        String id = "map";
		View view = new View(id, null, null);
		view.setTerritoryBlocked(DockingConstants.CENTER_REGION, true);
		view.setTitlebar(null);
		view.setContentPane(scp);
		return view;
    }

    protected JComponent createControl() {
        return create();
    }

}
