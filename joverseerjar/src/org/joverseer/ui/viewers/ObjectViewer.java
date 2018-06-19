package org.joverseer.ui.viewers;

import org.joverseer.ui.UISizes;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

/**
 * Base class for all viewers
 * Not really used
 * 
 * @author Marios Skounakis
 */
public abstract class ObjectViewer extends AbstractForm {
    
    public ObjectViewer(FormModel arg0, String arg1) {
        super(arg0, arg1);
        this.uiSizes = new UISizes();
    }

    public abstract boolean appliesTo(Object obj);
	
    public UISizes uiSizes;

	public UISizes getUiSizes() {
		return this.uiSizes;
	}

	public void setUiSizes(UISizes uiSizes) {
		this.uiSizes = uiSizes;
	}

}
