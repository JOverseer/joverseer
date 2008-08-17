package org.joverseer.ui.support.drawing;

import java.awt.Color;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import org.joverseer.domain.NationRelations;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

/**
 * ColorPicker
 * Users a properties file that serves as a MessageSource to retrieve colors
 * 
 * @author Marios Skounakis
 */
public class ColorPicker implements ApplicationListener {
    Hashtable color1 = new Hashtable();
    Hashtable color2 = new Hashtable();
    Hashtable colors = new Hashtable();

    private MessageSource colorSource = null;

    public ColorPicker() {
    }
    
    private void initHashTables() {
        color1 = new Hashtable();
        color2 = new Hashtable();
        colors = new Hashtable();
    }
    
    public static ColorPicker getInstance() {
        return (ColorPicker)Application.instance().getApplicationContext().getBean("colorPicker");
    }

    public Color getColor1(int nationNo) {
    	String key = String.valueOf(nationNo);
    	if (nationNo != 0) { // if known nation, get its allegiance, else keep key=0 to return color for unknown
	    	HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");                
	        if (mapOptions.containsKey(MapOptionsEnum.NationColors) && mapOptions.get(MapOptionsEnum.NationColors).equals(MapOptionValuesEnum.NationColorsAllegiance)) {
	        	NationRelations nr = (NationRelations)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
	        	key = nr.getAllegiance().toString();
	        }
    	}
        if (!color1.containsKey(key)) {
            String colorStr = null;
            GameTypeEnum gameType = GameHolder.instance().getGame().getMetadata().getGameType();
            try {
                colorStr = getColorSource().getMessage("nation." + key + "." + gameType.toString() + ".color", null, Locale.getDefault());
            }
            catch (Exception exc) {
            	try {
            		colorStr = getColorSource().getMessage("nation." + key + ".color", null, Locale.getDefault());
            	}
            	catch (Exception e) {
        			colorStr = getColorSource().getMessage(key + ".color", null, Locale.getDefault());
            	}
            };
            color1.put(key, Color.decode(colorStr));
        }
        return (Color)color1.get(key);
    }

    public Color getColor2(int nationNo) {
    	String key = String.valueOf(nationNo);
    	if (nationNo != 0) { // if known nation, get its allegiance, else keep key=0 to return color for unknown
	    	HashMap mapOptions = (HashMap)Application.instance().getApplicationContext().getBean("mapOptions");                
	        if (mapOptions.containsKey(MapOptionsEnum.NationColors) && mapOptions.get(MapOptionsEnum.NationColors).equals(MapOptionValuesEnum.NationColorsAllegiance)) {
	        	NationRelations nr = (NationRelations)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
	        	key = nr.getAllegiance().toString();
	        }
    	}
        if (!color2.containsKey(key)) {
            String colorStr = null;
            GameTypeEnum gameType = GameHolder.instance().getGame().getMetadata().getGameType();
            try {
                colorStr = getColorSource().getMessage("nation." + key + "." + gameType.toString() + ".color2", null, Locale.getDefault());
            }
            catch (Exception exc) {
            	try {
            		colorStr = getColorSource().getMessage("nation." + key + ".color2", null, Locale.getDefault());
            	}
            	catch (Exception e) {
        			colorStr = getColorSource().getMessage(key + ".color2", null, Locale.getDefault());
            	}
            };
            color2.put(key, Color.decode(colorStr));
        }
        return (Color)color2.get(key);
    }

    public Color getColor(String color) {
        if (!colors.containsKey(color)) {
            String colorStr = getColorSource().getMessage(color + ".color", null, Locale.getDefault());
            colors.put(color, Color.decode(colorStr));
        }
        return (Color)colors.get(color);
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.MapMetadataChangedEvent.toString())) {
                initHashTables();
            }
        }
    }

    MessageSource getColorSource() {
        if (colorSource == null) {
            colorSource = (MessageSource) Application.instance().getApplicationContext().getBean("colorSource");
        }
        return colorSource;
    }
    
}
