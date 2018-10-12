package org.joverseer.ui.support.controls;

import javax.swing.JButton;

import org.springframework.richclient.application.Application;

/**
 * Don't use this class.
 * Use
 *  = new JButton(Messages.getString("form.key"));
 *  instead.
 *  Then Eclipse can recognise the externalized string, 
 *  and the Eclipse Window Builder also recognised it.
 * 
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class ResourceButton extends JButton {

	public ResourceButton(String text) {
		super(Application.instance().getApplicationContext().getMessage(text, null, null));
	}

}
