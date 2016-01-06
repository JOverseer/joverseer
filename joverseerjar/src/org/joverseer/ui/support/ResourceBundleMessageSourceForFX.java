package org.joverseer.ui.support;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Our own version of the ResourceBundleMessageSource, that lets us get a specific ResourceBundle.
 * Needed to let javaFX FXML do it's internationalization.
 * @author Dave
 *
 */
public class ResourceBundleMessageSourceForFX extends ResourceBundleMessageSource {
	public ResourceBundle getResourceBundle(String basename)
	{
		return super.getResourceBundle(basename, Locale.getDefault());
	}
}
