package org.joverseer.ui.support.drawing;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Locale;

import org.joverseer.JOApplication;
import org.joverseer.domain.NationRelations;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
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
    //injected dependency
    final GameHolder gameHolder;

    public ColorPicker(GameHolder gameHolder) {
    	this.gameHolder = gameHolder;
    }

    private void initHashTables() {
        this.color1 = new Hashtable();
        this.color2 = new Hashtable();
        this.colors = new Hashtable();
    }

    public static ColorPicker getInstance() {
        return (ColorPicker)Application.instance().getApplicationContext().getBean("colorPicker");
    }

    public Color getColor1(int nationNo) {
    	String key = String.valueOf(nationNo);
    	if (nationNo != 0) { // if known nation, get its allegiance, else keep key=0 to return color for unknown
    		String pval = PreferenceRegistry.instance().getPreferenceValue("map.nationColors");
	    	if (pval.equals("allegiance")) {
	        	NationRelations nr = this.gameHolder.getGame().getTurn().getNationRelations(nationNo);
	        	key = nr.getAllegiance().toString();
	        }
    	}
        if (!this.color1.containsKey(key)) {
            String colorStr = null;
            GameTypeEnum gameType = this.gameHolder.getGame().getMetadata().getGameType();
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
            this.color1.put(key, Color.decode(colorStr));
        }
        return (Color)this.color1.get(key);
    }

    public Color getColor2(int nationNo) {
    	String key = String.valueOf(nationNo);
    	if (nationNo != 0) { // if known nation, get its allegiance, else keep key=0 to return color for unknown
	    	String pval = PreferenceRegistry.instance().getPreferenceValue("map.nationColors");
	        if (pval.equals("allegiance")) {
	        	NationRelations nr = this.gameHolder.getGame().getTurn().getNationRelations(nationNo);
	        	key = nr.getAllegiance().toString();
	        }
    	}
        if (!this.color2.containsKey(key)) {
            String colorStr = null;
            GameTypeEnum gameType = this.gameHolder.getGame().getMetadata().getGameType();
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
            this.color2.put(key, Color.decode(colorStr));
        }
        return (Color)this.color2.get(key);
    }

    public Color getColor(String color) {
        if (!this.colors.containsKey(color)) {
            String colorStr = getColorSource().getMessage(color + ".color", null, Locale.getDefault());
            this.colors.put(color, Color.decode(colorStr));
        }
        return (Color)this.colors.get(color);
    }

    @Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
        	this.onJOEvent((JOverseerEvent)applicationEvent);
        }
    }
    public void onJOEvent(JOverseerEvent e) {
    	switch (e.getType()) {
    	case MapMetadataChangedEvent:
            initHashTables();
        }
    }

    MessageSource getColorSource() {
        if (this.colorSource == null) {
            this.colorSource = JOApplication.getColorSource();
        }
        return this.colorSource;
    }


}
