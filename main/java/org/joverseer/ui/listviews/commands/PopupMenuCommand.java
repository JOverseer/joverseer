package org.joverseer.ui.listviews.commands;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;

public class PopupMenuCommand {
	public JComponent getButton(Object[] commands) {
	    JLabelButton popupMenu = new JLabelButton();
	    ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
	    Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
	    popupMenu.setIcon(ico);
	    final Object[] cmds = commands;
	    popupMenu.addActionListener(new PopupMenuActionListener() {
	
	        @Override
			public JPopupMenu getPopupMenu() {
	            CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
	                    "listViewCommandGroup", cmds);
	            return group.createPopupMenu();
	        }
	    });
	    return popupMenu;
	}
}
