package org.joverseer.ui.support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.WindowManager;

import com.formdev.flatlaf.FlatLaf;
import com.jgoodies.looks.BorderStyle;

/**
 * Hide/gather all the plaf knowledge.
 * Not ready for prime time yet ... ApplicationContext has a L&F configurer set.
 * @author Dave
 *
 */
public class PLaFHelper {
	HashMap<String,String> plaf = new HashMap();
	
	public PLaFHelper() {
		UIManager.LookAndFeelInfo[] builtinPlafInfos;
		builtinPlafInfos = UIManager.getInstalledLookAndFeels();
		//this.plaf.put("Default", "Default");
//        for (UIManager.LookAndFeelInfo info: builtinPlafInfos) {
//        	this.plaf.put(info.getName(), info.getClassName());
//        }
        if (isClassAvailable("com.formdev.flatlaf.FlatLightLaf")) {
        	this.plaf.put("Flat Light","com.formdev.flatlaf.FlatLightLaf");
        	this.plaf.put("Flat Dark","com.formdev.flatlaf.FlatDarkLaf");
//        	this.plaf.put("Darcula", "com.formdev.flatlaf.FlatDarculaLaf");
        }
//        if (isClassAvailable("com.jgoodies.looks.plastic.PlasticTheme")) {
//
//        	this.plaf.put("JGoodies Plastic","com.jgoodies.looks.plastic.PlasticLookAndFeel");
//        	this.plaf.put("JGoodies Plastic 3D","com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
//        	this.plaf.put("JGoodies Plastic XP","com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
//        	this.plaf.put("JGoodies Windows","com.jgoodies.looks.windows.WindowsLookAndFeel");

        	
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
//        }
        

	}
	public void fill(JComboBox combo) {
		for(Entry<String,String> entry:this.plaf.entrySet()) {
			combo.addItem(entry.getKey());
		}
	}
	public String fullClassFromName(String name)
	{
		return this.plaf.get(name);
	}
	public String nameFromClass(String clazz) {
		for(Entry<String,String> entry:this.plaf.entrySet()) {
			if (clazz.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return "";
	}
	/**
	 * call this when the Look and Feel has been changed, and force the UI to respond.
	 */
	public static void updateAll() {
		// Update the component tree (frame and its children)
		final WindowManager wm = Application.instance().getWindowManager();
		
//		if (isClassAvailable("com.jidesoft.plaf.LookAndFeelFactory")) {
//			// while we're still using jide components with extended L&F properties we need to add them...
//			//TODO: fix this so that the LookAndFeelFactory is invoked via reflection, so we can actually ignore jide one day:)
//			LookAndFeelFactory.installJideExtension(LookAndFeelFactory.XERTO_STYLE);
//		}
        
        FlatLaf.registerCustomDefaultsSource( "ui.themes");

        overwriteJIDELaF();

		for(ApplicationWindow w :wm.getWindows()) {
			SwingUtilities.updateComponentTreeUI(w.getControl());
	        // repack to resize 
	        w.getControl().pack();
	    }

	}
	
	public static void overwriteJIDELaF() {
		if(isDarkMode()) {
			/*
			 * Controls colors of the tabbing stuff
			 */
			Color selectedTabBackground = Color.decode("#1f2122");
			Color tabPaneBackground = Color.decode("#303335");
	
	        UIManager.put("DockableFrame.activeTitleBackground", tabPaneBackground);
	        UIManager.put("DockableFrame.inactiveTitleBackground", selectedTabBackground);
	        UIManager.put("DockableFrame.background", tabPaneBackground);
	        UIManager.put("DockableFrame.activeTitleBorderColor", selectedTabBackground);
	        UIManager.put("DockableFrame.inactiveTitleBorderColor", tabPaneBackground);
			
			UIManager.put("DockableFrame.titleBorder", BorderFactory.createEmptyBorder());
	
			UIManager.put("JideTabbedPane.tabAreaInsets", new Insets(0,0,0,0));
	        UIManager.put("JideTabbedPane.background", tabPaneBackground);
	        UIManager.put("JideTabbedPane.tabAreaBackground", tabPaneBackground);
			
	        //Selected tab background
	        UIManager.put("JideTabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
			UIManager.put("JideTabbedPane.shadow", selectedTabBackground);
			UIManager.put("JideTabbedPane.light", selectedTabBackground);
			
			//Tab Text unselected + selected
			UIManager.put("JideTabbedPane.unselectedTabTextForeground", UIManager.get("Label.foreground"));
			UIManager.put("JideTabbedPane.selectedTabTextForeground", UIManager.get("Label.foreground"));
			UIManager.put("DockableFrame.activeTitleForeground", UIManager.get("Label.foreground"));
			UIManager.put("DockableFrame.inactiveTitleForeground", UIManager.get("Label.foreground"));
		}
		
        UIManager.put("MenuItemUI", "com.formdev.flatlaf.ui.FlatMenuItemUI");
        UIManager.put("PopupMenuUI", "com.formdev.flatlaf.ui.FlatPopupMenuUI");
        UIManager.put("MenuUI", "com.formdev.flatlaf.ui.FlatMenuUI");
        
        UIManager.put("JidePopupMenuUI", "com.formdev.flatlaf.ui.FlatPopupMenuUI");
        UIManager.put("PopupMenuSeparatorUI", "com.formdev.flatlaf.ui.FlatSeparatorUI");
        UIManager.put("RadioButtonMenuItemUI", "com.formdev.flatlaf.ui.FlatRadioButtonMenuItemUI");
        UIManager.put("CheckBoxMenuItemUI", "com.formdev.flatlaf.ui.FlatCheckBoxMenuItemUI");
//        UIManager.put("PopupMenu.border", "com.formdev.flatlaf.ui.FlatPopupMenuBorderUI");
        
//        UIManager.put("Menu.selectionBackground", UIManager.getColor("MenuItem.selectionBackground")); // also match text color
//        UIManager.put("Menu.selectionForeground", UIManager.getColor("MenuItem.selectionForeground")); // also match text color

        
        Icon blankIcon = new Icon() {
		    @Override
		    public int getIconWidth() {
		        return 0; // or desired width
		    }

		    @Override
		    public int getIconHeight() {
		        return 0; // or desired height
		    }

		    @Override
		    public void paintIcon(Component c, Graphics g, int x, int y) {
		        // draw nothing
		    }
		};
        
        UIManager.put("MenuItem.checkIcon", blankIcon);
        UIManager.put("Menu.checkIcon", blankIcon);
        UIManager.put("MenuItem.checkIconGap", 0);
        UIManager.put("Menu.checkIconGap", 0);
	}
	
	/*
	 * Debugging function that wouldnt compile
	 */
//    public static void checkLaFTheme(Component component) {
//        if (component instanceof JComponent) {
//            JComponent jComponent = (JComponent) component;
//            // Check if the component is using a custom Look and Feel
//            if(jComponent.getUI() == null) {}
//            else if (!(jComponent.getUI().getClass().getName().contains("com.formdev.flatlaf.ui"))) {
//                System.out.println(component.getClass().getName() + " is not using the default LaF theme." + jComponent.getUI().getClass().getName());
//            }
//        }
//            if (component instanceof Container) {
//                for (Component child : ((Container) component).getComponents()) {
//                    checkLaFTheme(child);  // Recursive call to check the child component
//                }
//            }
//    }

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
	
	public static boolean isDarkMode() {
		if(UIManager.getLookAndFeel().getClass().getName().equals("com.formdev.flatlaf.FlatDarkLaf")) return true;
		else return false;
	}

}
