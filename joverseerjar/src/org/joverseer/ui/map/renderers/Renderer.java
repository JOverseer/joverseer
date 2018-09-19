package org.joverseer.ui.map.renderers;

import java.awt.*;

/**
 * Interface for all renderers
 * 
 * @author Marios Skounakis
 */
public interface Renderer {
	public void refreshConfig();
    public boolean appliesTo(Object ojb);

    public void render(Object obj, Graphics2D g, int x, int y);
}
