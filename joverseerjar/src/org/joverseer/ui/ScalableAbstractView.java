package org.joverseer.ui;

import org.springframework.richclient.application.support.AbstractView;

public abstract class ScalableAbstractView extends AbstractView {
	public UISizes uiSizes;

	public UISizes getUiSizes() {
		return this.uiSizes;
	}

	public void setUiSizes(UISizes uiSizes) {
		this.uiSizes = uiSizes;
	}
	public ScalableAbstractView() {
		this.uiSizes = new UISizes();
	}

}
