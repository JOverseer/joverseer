package org.springframework.richclient.flexdock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveFactory;

public class FlexPerspectiveFactory implements PerspectiveFactory {
	private Map<String, Perspective> perspectives = new HashMap<String, Perspective>();

	public Perspective getPerspective(String persistentId) {
		if (perspectives.containsKey(persistentId))
			return perspectives.get(persistentId);
		else {
			throw new InvalidPerspectiveIdException(persistentId + " is not a valid perspective id!");
		}
	}
	 
	/**
	 * We expect a map with (perspepective id, list of docking states)
	 * @param dockingStates
	 */
    public void setPerspectivesMetaInfos(Map<String, List<DockingState>> perspectivesMetaInfos) {
    	Iterator<String> i = perspectivesMetaInfos.keySet().iterator();
        while(i.hasNext()) {
            String key = i.next();
            List<DockingState> dockingStates = perspectivesMetaInfos.get(key);
            perspectives.put(key, createPerspective(key, dockingStates));
        }
    }
	    
    private Perspective createPerspective(String perspectiveId, List<DockingState> dockingStates) {
        Perspective perspective = new Perspective(perspectiveId, perspectiveId);
		LayoutSequence sequence = perspective.getInitialSequence(true);
		for (DockingState dockingState : dockingStates) {
		    if( dockingState.getRelativeParentId() != null)
		        sequence.add(dockingState.getDockableId(), dockingState.getRelativeParentId(), dockingState.getRegion(), dockingState.getSplitRatio());
		    else
		        sequence.add(dockingState.getDockableId());
		}
		return perspective;
    }
    
    public Map<String, Perspective> getPerspectives() {
        return perspectives;
    }
}
