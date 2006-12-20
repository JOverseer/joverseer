package org.joverseer.ui.map.renderers;

import java.awt.*;


public interface Renderer {
    public boolean appliesTo(Object ojb);

    public void render(Object obj, Graphics2D g, int x, int y);
}
