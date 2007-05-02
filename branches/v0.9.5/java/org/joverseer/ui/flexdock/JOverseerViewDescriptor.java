package org.joverseer.ui.flexdock;

import org.springframework.richclient.application.flexdock.FlexDockViewDescriptor;


public class JOverseerViewDescriptor extends FlexDockViewDescriptor {
    private boolean hasTitleBar = true;

    public boolean getHasTitleBar() {
        return hasTitleBar;
    }

    public void setHasTitleBar(boolean hasTitleBar) {
        this.hasTitleBar = hasTitleBar;
    }


}
