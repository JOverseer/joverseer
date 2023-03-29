package org.joverseer.ui.support;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.WindowManager;

/**
 * Hide/gather all the plaf knowledge.
 * Not ready fro prime time yet ... ApplicationContext has a L&F configurer set.
 * @author Dave
 *
 */
public class PLaFHelper {
	UIManager.LookAndFeelInfo[] builtinPlafInfos;
	String[] plafNames;
	HashMap<String,String> plaf = new HashMap();
	
	public PLaFHelper() {
		this.builtinPlafInfos = UIManager.getInstalledLookAndFeels();
        for (int ii=0; ii<this.builtinPlafInfos.length; ii++) {
        	this.plaf.put(this.builtinPlafInfos[ii].getName(), this.builtinPlafInfos[ii].getClassName());
        }
        if (isClassAvailable("com.formdev.flatlaf.FlatLightLaf")) {
        	this.plaf.put("Flat Light","com.formdev.flatlaf.FlatLightLaf");
        	this.plaf.put("Flat Dark","com.formdev.flatlaf.FlatDarkLaf");
        }
        if (isClassAvailable("com.jgoodies.looks.plastic.PlasticTheme")) {

        	this.plaf.put("JGoodies Plastic","com.jgoodies.looks.plastic.PlasticLookAndFeel");
        	this.plaf.put("JGoodies Plastic 3D","com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        	this.plaf.put("JGoodies Plastic XP","com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        	this.plaf.put("JGoodies Windows","com.jgoodies.looks.windows.WindowsLookAndFeel");

        	
        	/*
        	 * com.jgoodies.looks.plastic.theme
        	 */
/*        	
        	this.plaf.put("BrownSugar","com.jgoodies.looks.plastic.theme.BrownSugar");
        	this.plaf.put("DarkStar","com.jgoodies.looks.plastic.theme.DarkStar");
        	this.plaf.put("DesertBlue","com.jgoodies.looks.plastic.theme.DesertBlue");
        	this.plaf.put("DesertBluer","com.jgoodies.looks.plastic.theme.DesertBluer");
        	this.plaf.put("DesertGreen","com.jgoodies.looks.plastic.theme.DesertGreen");
        	this.plaf.put("DesertRed","com.jgoodies.looks.plastic.theme.DesertRed");
        	this.plaf.put("DesertYellow","com.jgoodies.looks.plastic.theme.DesertYellow");
        	this.plaf.put("ExperienceBlue","com.jgoodies.looks.plastic.theme.ExperienceBlue");
        	this.plaf.put("ExperienceGreen","com.jgoodies.looks.plastic.theme.ExperienceGreen");
        	this.plaf.put("ExperienceRoyale","com.jgoodies.looks.plastic.theme.ExperienceRoyale");
        	this.plaf.put("LightGray","com.jgoodies.looks.plastic.theme.LightGray");
        	this.plaf.put("Silver","com.jgoodies.looks.plastic.theme.Silver");
        	this.plaf.put("SkyBlue","com.jgoodies.looks.plastic.theme.SkyBlue");
        	this.plaf.put("SkyBluer","com.jgoodies.looks.plastic.theme.SkyBluer");
        	this.plaf.put("SkyKrupp","com.jgoodies.looks.plastic.theme.SkyKrupp");
        	this.plaf.put("SkyPink","com.jgoodies.looks.plastic.theme.SkyPink");
        	this.plaf.put("SkyRed","com.jgoodies.looks.plastic.theme.SkyRed");
        	this.plaf.put("SkyYellow","com.jgoodies.looks.plastic.theme.SkyYellow");
        	*/
        }
        

	}
	public void fill(JComboBox combo) {
		for(Entry<String,String> entry:this.plaf.entrySet()) {
			combo.addItem(entry.getKey());
		}
	}
	public String fullClassFromName(String name)
	{
		return plaf.get(name);
	}
	public String nameFromClass(String clazz) {
		for(Entry<String,String> entry:this.plaf.entrySet()) {
			if (clazz.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return "";
	}
	// this is going to be slow!
	public void updateAll() {
		// Update the component tree (frame and its children)
		final WindowManager wm = Application.instance().getWindowManager();
		for(ApplicationWindow w :wm.getWindows()) {
			SwingUtilities.updateComponentTreeUI(w.getControl());
	        // repack to resize 
	        w.getControl().pack();
	    }
	}

	public boolean isClassAvailable(String clazz) {
		Class candidate;
		boolean result = false;
		try {
			candidate = Class.forName(clazz);
			result = true;
		} catch (Exception e) {
			// do nothing.
		} finally {
			candidate = null; // notionally release
		}
		return result;
	}

}
