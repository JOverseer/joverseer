package org.joverseer.ui.support.controls;

import javax.swing.JLabel;

import org.springframework.richclient.application.Application;

public class ResourceLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/**
	 * Don't use this class.
	 * Use
	 *  = new JLabel(Messages.getString("form.key"));
	 *  instead.
	 *  Then Eclipse can recognise the externalized string, 
	 *  and the Eclipse Window Builder also recognised it.
	 *
	 */
	@Deprecated
	public ResourceLabel(String text) {
		super(Application.instance().getApplicationContext().getMessage(text, null, null));
	}

}
