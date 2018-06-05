package org.joverseer;

import java.util.HashMap;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.image.ImageSource;

public class joApplication {

	// this is a class to make it clear that all the Application.instance().getApplicationContext(). are one and the same
	// and reduce the amount of casting going on.
	public joApplication() {
		// TODO Auto-generated constructor stub

	}
	static public ApplicationContext getApplicationContext()
	{
		return Application.instance().getApplicationContext();
	}
	static public ApplicationDescriptor getApplicationDescriptor()
	{
		return (ApplicationDescriptor)getApplicationContext().getBean("applicationDescriptor");
	}

	static public GameHolder getGameHolder()
	{
		return GameHolder.instance();
	}
	static public Game getGame()
	{
		return getGameHolder().getGame();
	}
	static public GameMetadata getMetadata()
    {
    	return getGame().getMetadata();
    }
    static public MapMetadata getMapMetadata()
    {
    	 return (MapMetadata) getApplicationContext().getBean("mapMetadata"); //$NON-NLS-1$
    }
    public static ImageSource getImageSource()
    {
    	return (ImageSource) getApplicationContext().getBean("imageSource"); //$NON-NLS-1$
    }
    public static MessageSource getColorSource()
    {
    	return (MessageSource)getApplicationContext().getBean("colorSource"); //$NON-NLS-1$
    }

	public static HashMap getMapOptions()
	{
		return (HashMap) getApplicationContext().getBean("mapOptions"); //$NON-NLS-1$
	}
	public static HashMap getMapEditorOptions()
	{
		return (HashMap) getApplicationContext().getBean("mapEditorOptions"); //$NON-NLS-1$

	}
	
    static public void publishEvent(LifecycleEventsEnum type,Object object,Object sender)
	{
		getApplicationContext().publishEvent(new JOverseerEvent(type, object,sender));
	}
	static public void publishEvent(LifecycleEventsEnum type,Object object)
	{
		getApplicationContext().publishEvent(new JOverseerEvent(type, object,null));
	}

}