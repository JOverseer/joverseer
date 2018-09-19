package org.joverseer.ui.map.renderers;

import org.joverseer.ui.map.MapMetadata;

public abstract class AbstractBaseRenderer implements Renderer {
	// injected dependency
	MapMetadata mapMetadata = null;

	public MapMetadata getMapMetadata() {
		return this.mapMetadata;
	}

	public void setMapMetadata(MapMetadata mapMetadata) {
		this.mapMetadata = mapMetadata;
	}

	public AbstractBaseRenderer() {
	}

}
