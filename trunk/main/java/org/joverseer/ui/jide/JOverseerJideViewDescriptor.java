package org.joverseer.ui.jide;

import com.jidesoft.spring.richclient.docking.view.JideViewDescriptor;

/**
 * Specializes JideViewDescriptor
 * 
 * @author Marios Skounakis
 */
public class JOverseerJideViewDescriptor extends JideViewDescriptor {

    int preferredWidth = 100;
    int preferredHeight = 100;
    String viewGroup;

    public int getPreferredHeight() {
        return this.preferredHeight;
    }

    public void setPreferredHeight(int preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public int getPreferredWidth() {
        return this.preferredWidth;
    }

    public void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public String getViewGroup() {
        return this.viewGroup;
    }

    public void setViewGroup(String viewGroup) {
        this.viewGroup = viewGroup;
    }


}
