package org.joverseer.ui.flexdock;


import java.util.ArrayList;
import java.util.List;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveFactory;

public class JOverseerPerspectiveFactory implements PerspectiveFactory {

    public Perspective getPerspective(String persistentId) {
        // Perspective perspective = new Perspective(persistentId, "test");
        // LayoutSequence sequence = perspective.getInitialSequence(true);

        List<DockingState> dss = new ArrayList<DockingState>();

        DockingState ds = new DockingState("mapView");
        dss.add(ds);

        ds = new DockingState("characterListView");
        ds.setRegion(DockingConstants.SOUTH_REGION);
        ds.setRelativeParentId("mapView");
        ds.setSplitRatio(.3f);
        dss.add(ds);

        ds = new DockingState("populationCenterListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);

        ds = new DockingState("nationEconomyListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);

        ds = new DockingState("nationMessageListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);

        ds = new DockingState("artifactListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);
        
        ds = new DockingState("artifactInfoListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);


        ds = new DockingState("orderListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);
        
        ds = new DockingState("companyListView");
        ds.setRegion(DockingConstants.CENTER_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(1f);
        dss.add(ds);

        ds = new DockingState("currentHexDataViewer");
        ds.setRegion(DockingConstants.EAST_REGION);
        ds.setRelativeParentId("mapView");
        ds.setSplitRatio(.3f);
        dss.add(ds);

        ds = new DockingState("mapOptionsView");
        ds.setRegion(DockingConstants.EAST_REGION);
        ds.setRelativeParentId("characterListView");
        ds.setSplitRatio(.01f);
        dss.add(ds);

        //
        // // DefaultDockingPort dp = new DefaultDockingPort();
        // // dp.setPreferredSize(new Dimension(1000,800));
        // // sequence.apply(dp);
        // sequence.add("mapView");
        // //sequence.add("turnSelectorView", "mapView", DockingConstants.EAST_REGION, .01f);
        // sequence.add("currentHexDataViewer", "mapView", DockingConstants.EAST_REGION, .1f);
        // sequence.add("characterListView", "mapView", DockingConstants.SOUTH_REGION, .001f);
        // //sequence.add("turnSelectorView", "currentHexDataViewer", DockingConstants.SOUTH_REGION, .1f);
        // //sequence.add("populationCenterListView", "mapView", DockingConstants.SOUTH_REGION, .1f);
        // return perspective;
        return createPerspective("joverseer", dss);
    }

    private Perspective createPerspective(String perspectiveId, java.util.List<DockingState> dockingStates) {
        Perspective perspective = new Perspective(perspectiveId, perspectiveId);
        LayoutSequence sequence = perspective.getInitialSequence(true);
        for (DockingState dockingState : dockingStates) {
            if (dockingState.getRelativeParentId() != null)
                sequence.add(dockingState.getDockableId(), dockingState.getRelativeParentId(),
                        dockingState.getRegion(), dockingState.getSplitRatio());
            else
                sequence.add(dockingState.getDockableId());
        }
        return perspective;
    }
}