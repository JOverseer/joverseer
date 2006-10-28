package org.joverseer.ui;

import org.springframework.richclient.application.flexdock.FlexDockViewDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 28 Οκτ 2006
 * Time: 9:04:37 μμ
 * To change this template use File | Settings | File Templates.
 */
public class JOverseerViewDescriptor extends FlexDockViewDescriptor {
    private boolean hasTitleBar = true;

    public boolean getHasTitleBar() {
        return hasTitleBar;
    }

    public void setHasTitleBar(boolean hasTitleBar) {
        this.hasTitleBar = hasTitleBar;
    }


}
