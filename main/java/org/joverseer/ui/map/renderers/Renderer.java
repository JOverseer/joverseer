package org.joverseer.ui.map.renderers;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 6:16:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Renderer {
    public boolean appliesTo(Object ojb);

    public void render(Object obj, Graphics2D g, int x, int y);
}
