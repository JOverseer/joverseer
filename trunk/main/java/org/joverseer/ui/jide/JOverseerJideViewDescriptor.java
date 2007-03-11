package org.joverseer.ui.jide;

import com.jidesoft.spring.richclient.docking.view.JideViewDescriptor;

public class JOverseerJideViewDescriptor extends JideViewDescriptor {
	int preferredWidth;
	int preferredHeight;
	public int getPreferredHeight() {
		return preferredHeight;
	}
	public void setPreferredHeight(int preferredHeight) {
		this.preferredHeight = preferredHeight;
	}
	public int getPreferredWidth() {
		return preferredWidth;
	}
	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}
	
	
}
