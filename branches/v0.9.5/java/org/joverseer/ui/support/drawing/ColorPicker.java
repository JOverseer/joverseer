package org.joverseer.ui.support.drawing;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;

import sun.applet.AppletListener;

import java.util.Hashtable;
import java.util.Locale;
import java.awt.*;


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
        if (!color1.containsKey(nationNo)) {
            String colorStr = null;
            GameTypeEnum gameType = GameHolder.instance().getGame().getMetadata().getGameType();
            try {
                colorStr = getColorSource().getMessage("nation." + nationNo + "." + gameType.toString() + ".color", null, Locale.getDefault());
            }
            catch (Exception exc) {
                colorStr = getColorSource().getMessage("nation." + nationNo + ".color", null, Locale.getDefault());
            };
            color1.put(nationNo, Color.decode(colorStr));
        }
        return (Color)color1.get(nationNo);
    }

    public Color getColor2(int nationNo) {
        if (!color2.containsKey(nationNo)) {
            String colorStr = null;
            GameTypeEnum gameType = GameHolder.instance().getGame().getMetadata().getGameType();
            try {
                colorStr = getColorSource().getMessage("nation." + nationNo + "." + gameType.toString() + ".color2", null, Locale.getDefault());
            }
            catch (Exception exc) {
                colorStr = getColorSource().getMessage("nation." + nationNo + ".color2", null, Locale.getDefault());
            };
            color2.put(nationNo, Color.decode(colorStr));
        }
        return (Color)color2.get(nationNo);
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
