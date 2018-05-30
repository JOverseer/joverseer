package org.joverseer.ui;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;


public abstract class ScalableAbstractForm extends AbstractForm {

	public UISizes uiSizes;

	public ScalableAbstractForm(FormModel arg0, String fORM_PAGE) {
		super(arg0,fORM_PAGE);
		this.uiSizes = new UISizes();
	}

	public UISizes getUiSizes() {
		return this.uiSizes;
	}

	public void setUiSizes(UISizes uiSizes) {
		this.uiSizes = uiSizes;
	}
}
