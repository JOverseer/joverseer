package org.joverseer.ui;


import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveFactory;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;

import java.awt.*;

public class SamplePerspectiveFactory implements PerspectiveFactory {

    public Perspective getPerspective(String persistentId) {
        Perspective perspective = new Perspective(persistentId, "test");
        LayoutSequence sequence = perspective.getInitialSequence(true);


//        DefaultDockingPort dp = new DefaultDockingPort();
//        dp.setPreferredSize(new Dimension(1000,800));
//        sequence.apply(dp);
        sequence.add("mapView");
        //sequence.add("turnSelectorView", "mapView", DockingConstants.EAST_REGION, .01f);
        sequence.add("currentHexDataViewer", "mapView", DockingConstants.EAST_REGION, .1f);
        sequence.add("characterListView", "currentHexDataViewer", DockingConstants.EAST_REGION, .1f);
        sequence.add("turnSelectorView", "currentHexDataViewer", DockingConstants.SOUTH_REGION, .1f);
        sequence.add("populationCenterListView", "currentHexDataViewer", DockingConstants.SOUTH_REGION, .1f);
        return perspective;
    }
}
