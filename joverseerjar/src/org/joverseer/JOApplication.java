package org.joverseer;

import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoRegistry;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.combatCalculator.CombatFormHolder;
import org.joverseer.ui.jide.JOverseerJideViewDescriptor;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationDescriptor;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.image.ImageSource;

public class JOApplication {

	// this is a class to make it clear that all the Application.instance().getApplicationContext(). are one and the same
	// and reduce the amount of casting going on.
	// also limits the impact of the decision to create singletons, to this class, instead of getInstance() functions on
	// all other classes, and removes the knowledge of a singleton class from classes that don't really need to know.
	// it also hides the details of the application context from classes that don't need to know.
	public JOApplication()
	{
	}
	static public ApplicationContext getApplicationContext()
	{
		return Application.instance().getApplicationContext();
	}
	static public ApplicationDescriptor getApplicationDescriptor()
	{
		return (ApplicationDescriptor)getApplicationContext().getBean("applicationDescriptor");
	}

	public static Icon getIcon(OrderResultTypeEnum orderResultType) {
		if (orderResultType == null) {
			return null;
		}
		Icon ico = null;
		ImageSource imgSource = JOApplication.getImageSource();
		if (imgSource != null) {
			String iconKey= null;
			switch (orderResultType) {
			case Info: iconKey= "orderresult.info.icon"; break;
			case Help: iconKey= "orderresult.help.icon"; break;
			case Warning: iconKey= "orderresult.warn.icon"; break;
			case Error: iconKey= "orderresult.error.icon"; break;
			case Okay: iconKey= "orderresult.okay.icon"; break;
			}
			if (iconKey!=null) {
				ico = new ImageIcon(imgSource.getImage(iconKey));
			}
		}
		return ico;
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
    public static InfoRegistry getInfoRegistry()
    {
        return (InfoRegistry) getApplicationContext().getBean("infoRegistry"); //$NON-NLS-1$
    }

    public static CombatFormHolder getCombatFormHolder() {
        return (CombatFormHolder) getApplicationContext().getBean("combatCalculatorHolder");
    }


    static public void publishEvent(LifecycleEventsEnum type,Object object,Object sender)
	{
		getApplicationContext().publishEvent(new JOverseerEvent(type, object,sender));
	}
	static public void publishEvent(LifecycleEventsEnum type,Object object)
	{
		getApplicationContext().publishEvent(new JOverseerEvent(type, object,null));
	}
	/*
	 * Find a specific page by view name.
	 * Use sparingly and consider using publishEvent instead.
	 * consider impact of threads and circular dependencies.
	 * Created to allow togglePC to be lost from CurrentHexView to efficiently
	 * trigger EconomyCalculator.
	 */
	public static JOverseerJideViewDescriptor findViewInstance(String desiredViewId) {
		ViewDescriptorRegistry viewDescriptorRegistry = (ViewDescriptorRegistry) ApplicationServicesLocator.services().getService(ViewDescriptorRegistry.class);
		ViewDescriptor[] views = viewDescriptorRegistry.getViewDescriptors();
		for (int i = 0; i < views.length; i++) {
			ViewDescriptor candidateView = views[i];
			if(candidateView instanceof JOverseerJideViewDescriptor){
				JOverseerJideViewDescriptor view = (JOverseerJideViewDescriptor)candidateView;
				if (view.getId().equalsIgnoreCase(desiredViewId)) {
					return view;
				}
			}
		}
		return null;
    }
	public static boolean isApplicationLoaded() {
		return Application.isLoaded();
	}
}
