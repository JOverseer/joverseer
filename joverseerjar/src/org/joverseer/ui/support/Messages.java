package org.joverseer.ui.support;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.richclient.application.Application;

// an adapter for a spring MessageSource, so that Eclipse can manage the externalized strings for us.
public class Messages {
	private static final String MESSAGEBEAN = "messageSource"; //$NON-NLS-1$

	private Messages() {
	}

	public static String getString(String key, Object[] args,String defaultMessage) {
		return getMessageSource().getMessage(key, args, defaultMessage,Locale.getDefault());
	}
	public static String getString(String key) {
		return getString(key, new Object[] {});
	}

	public static String getString(String key, Object[] args) {
		try {
			return getMessageSource().getMessage(key, args, Locale.getDefault());
		} catch (NoSuchMessageException e) {
			return "!"+key+"!";
		}
		
	}
	public static MessageSource getMessageSource()
	{
		return (MessageSource) Application.instance().getApplicationContext().getBean(MESSAGEBEAN);
	}
}
